package com.prodia.technical.model.request.update;

import com.prodia.technical.model.request.create.CreateDiagnoseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDiagnoseRequest extends CreateDiagnoseRequest {

  @NotBlank(message = "id cannot be blank")
  private String id;
  @NotNull(message = "version cannot be null")
  private Long version;
}
