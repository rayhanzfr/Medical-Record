package com.prodia.technical.logging.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestApiLogRequest {

  @NotBlank(message = "user cannot be blank")
  private String user;

  @Nullable
  private Long startEpoch;

  @Nullable
  private Long endEpoch;

}
