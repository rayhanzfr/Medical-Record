package com.prodia.technical.logging.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityAuditTrailEvent extends AbstractLogEvent {

  private transient Object entity;
  private Action action;

  public EntityAuditTrailEvent(Object source) {
    super(source);
  }

  public enum Action {
    CREATE,
    DELETE,
    UPDATE
  }

}