package com.prodia.technical.model.request.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDiagnoseRequest {

  private String medicalRecordId;
  @NotBlank(message = "code cannot be blank")
  private String code;
  @NotBlank(message = "description cannot be blank")
  private String description;
}
