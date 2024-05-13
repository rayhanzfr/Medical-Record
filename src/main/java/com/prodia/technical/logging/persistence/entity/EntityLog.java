package com.prodia.technical.logging.persistence.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("entity_logs")
public class EntityLog extends AbstractLogDocument {

  @Id
  private String id;

  private String action;

  private org.bson.Document entity;

  private String module;

  private String tableName;

  private String entityName;

}
