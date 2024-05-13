package com.prodia.technical.logging.persistence.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityLogDetailBeforeAfter {
  private String action;
  private Date timestamp;
  private org.bson.Document entity;
  private String[] difference;

}
