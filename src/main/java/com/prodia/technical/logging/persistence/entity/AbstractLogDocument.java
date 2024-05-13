package com.prodia.technical.logging.persistence.entity;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractLogDocument {

  private String applicationName;

  private Instant timestamp;

  private String tenantId;

  private String user;

}
