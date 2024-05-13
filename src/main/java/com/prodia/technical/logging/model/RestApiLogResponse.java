package com.prodia.technical.logging.model;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestApiLogResponse {

  private String applicationName;
  private String applicationVersion;
  private String httpMethod;
  private String protocol;
  private String uri;
  private String queryParam;
  private Map<String, Object> request;
  private Map<String, Object> response;
  private Long timestamp;

}
