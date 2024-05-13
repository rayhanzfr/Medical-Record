package com.prodia.technical.logging.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public abstract class AbstractLogEvent extends ApplicationEvent {

  private String tenantId;

  private String user;

  protected AbstractLogEvent(Object source) {
    super(source);
  }

}