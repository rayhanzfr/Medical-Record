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
@Document("api_logs")
public class ApiLog extends AbstractLogDocument {

  @Id
  private String id;

  private String protocol;

  private String httpMethod;

  private String uri;

  private String queryParam;

  private org.bson.Document request;

  private org.bson.Document response;

}
