package com.prodia.technical.common.properties;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
@ConfigurationProperties("prodia.logging")
public class LoggingProperties {

  @NotNull
  private Boolean persistence;

  @NotNull
  private Boolean restApi;

}
