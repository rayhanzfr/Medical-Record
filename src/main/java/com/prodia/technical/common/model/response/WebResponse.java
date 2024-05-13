package com.prodia.technical.common.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse<T> {

  /**
   * Code , usually same as HTTP Code
   */
  @JsonProperty("code")
  private Integer code;

  /**
   * Status, usually same as HTTP status
   */
  @JsonProperty("status")
  private String status;

  /**
   * Dynamic Column
   */
  @JsonProperty("column")
  private List<CustomColumn> column;

  /**
   * Response data
   */
  @JsonProperty("data")
  private T data;

  /**
   * Paging information, if response is paginate data
   */
  @JsonProperty("paging")
  private PagingResponse paging;

  /**
   * Error information, if request is not valid
   */
  @JsonProperty("errors")
  private Map<String, List<String>> errors;

  /**
   * Metadata information
   */
  @JsonProperty("metadata")
  private Map<String, Object> metadata;
}