package com.prodia.technical.logging.persistence.repository;

import com.prodia.technical.logging.persistence.entity.EntityLog;
import com.prodia.technical.logging.persistence.entity.EntityLogAggregate;
import com.prodia.technical.logging.persistence.entity.EntityLogDetail;
import java.time.Instant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityLogRepository extends MongoRepository<EntityLog, String> {

  @Aggregation(pipeline = {
      """
            {
                $match: {
                  $and: [
                    { tenantId: ?0 },
                    { timestamp: { $gte: ?1, $lt:?2 } },
                    { $or: [ { $expr: { $eq: ['?3', 'null'] } }, { user: { $regex: '^?3$', $options: 'i'} } ] },
                    { $or: [ { $expr: { $eq: ['?4', 'null'] } }, { module: { $regex: '^?4$', $options: 'i'} } ] }
                  ]
                }
            }
          """,
      "{ $sort: { timestamp: -1} }", """
          {
            $group: {
              _id: {
                date: {
                  $dateToString: {
                    format:'%d-%m-%Y',
                    date: '$timestamp',
                    timezone: '+07:00'
                  }
                },
                action: '$action',
                user: '$entity.updatedBy',
                module: '$module',
                entityName: '$entityName'
              },
              entities: {
                $push: {
                  _id: '$_id',
                  action: '$action',
                  module: '$module',
                  tableName: '$tableName',
                  entityName: '$entityName',
                  applicationName: '$applicationName',
                  timestamp: '$timestamp',
                  tenantId: '$tenantId',
                  user: '$user'
                }
              }
            }
          }
          """, """
          {
                 $addFields: {
                     'dateFormatted': {
                         $concat: [
                             { $substr: ['$_id.date', 6, 4] },
                             '-',
                             { $substr: ['$_id.date', 3, 2] },
                             '-',
                             { $substr: ['$_id.date', 0, 2] }
                         ]
                     }
                 }
             }
          """, "{ $sort: {'dateFormatted': -1} }", """
          { $project: { _id: 1, entities: 1 } }
          """})
  Slice<EntityLogAggregate> findAllByFilter(String tenantId, Instant from, Instant to, String user,
      String module, Pageable pageable);

  @Aggregation(pipeline = {
      """
          {
                 $match: {
                     $and: [
                         { tenantId: ?0 },
                         {
                             $expr: {
                                 $and: [
                                     { $eq: [{ $dateToString: { format: "%d-%m-%Y", date: "$timestamp" } }, ?1] },
                                     {
                                         $regexMatch: {
                                             input: "$user",
                                             regex: ?2,
                                             options: "i"
                                         }
                                     },
                                     { $eq: ["$module", ?3] },
                                     { $eq: ["$entityName", ?4] },
                                     { $eq: ["$action",?5] },
                                 ]
                             }
                         }
                     ]
                 }
             }
          """,
      "{$sort:{timestamp:-1}}",
      """
          {
                 $lookup: {
                     from: 'entity_logs',
                     let: { timestampHeader: '$timestamp', tableNameHeader: '$tableName', entityIdHeader: '$entity.id' },
                     pipeline: [
                         {
                             $match: {
                                 $expr: {
                                     $and: [
                                         { $eq: ["$tableName", "$$tableNameHeader"] },
                                         { $eq: ["$entity.id", "$$entityIdHeader"] },
                                         { $lt: ["$timestamp", "$$timestampHeader"] },
                                     ]
                                 }
                             }
                         },
                         { $limit: 1 }
                     ],
                     as: 'subqueryResultAfter'
                 }
             }
             """,
      """
          {
              $project: {
                  tableName: '$tableName',
                  applicationName: '$applicationName',
                  module: '$module',
                  entityName: '$entityName',
                  after: {
                      action: '$action',
                      timestamp: '$timestamp',
                      entity: '$entity',
                      difference: {
                      $map: {
                         input: { $setDifference: [{ $objectToArray: "$entity" }, { $objectToArray: { $arrayElemAt: ['$subqueryResultAfter.entity', 0] } }] },
                         as: "item",
                         in: "$$item.k"
                          }
                      }
                 },
                  before: {
                      action: { $arrayElemAt: ['$subqueryResultAfter.action', 0] },
                      timestamp: { $arrayElemAt: ['$subqueryResultAfter.timestamp', 0] },
                      entity: { $arrayElemAt: ['$subqueryResultAfter.entity', 0] }
                  },

              }
          }
          """})
  Slice<EntityLogDetail> findDetailBy(String TenantId, String date, String user, String module,
      String entityName, String action, Pageable pageable);
}
