package com.prodia.technical.logging.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityLogAggregateId {

  private String date;
  private String action;
  private String user;
  private String module;
  private String entityName;

}
