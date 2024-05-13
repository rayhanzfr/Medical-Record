package com.prodia.technical.logging.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityLogDetail {
  private String tableName;
  private String applicationName;
  private String module;
  private String entityName;
  private EntityLogDetailBeforeAfter after;
  private EntityLogDetailBeforeAfter before;
}
