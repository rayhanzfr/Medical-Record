package com.prodia.technical.logging.persistence.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityLogAggregate {

  private @Id EntityLogAggregateId id;
  private List<EntityLog> entities;

}
