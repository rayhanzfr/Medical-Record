package com.prodia.technical.logging.persistence.repository;

import com.prodia.technical.logging.persistence.entity.ApiLog;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiLogRepository extends MongoRepository<ApiLog, String> {

  @Query(
    value = "{ user: { $regex: '^?0$', $options: 'i'}, tenantId: { $eq:  ?1 }, timestamp: { $gte: ?2, $lt: ?3 } }",
    sort = "{ timestamp: -1 }"
  )
  Page<ApiLog> findAllBy(String user, String tenantId, Instant from, Instant to, Pageable pageable);

}
