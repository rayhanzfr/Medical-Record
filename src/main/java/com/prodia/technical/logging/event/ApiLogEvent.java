package com.prodia.technical.logging.event;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.springframework.http.HttpStatus;


@Getter
@Setter
public class ApiLogEvent extends AbstractLogEvent {

  private String requestId;

  private String protocol;

  private String httpMethod;

  private String uri;

  private String queryParam;

  private HttpStatus statusCode;

  private Document request;

  private Document response;

  public ApiLogEvent(Object source) {
    super(source);
  }

}
