package com.prodia.technical.model.request.create;

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
public class CreateRecipeMedicineRequest {

  private String medicalRecordId;
  @NotBlank(message = "name cannot be blank")
  private String name;
  @NotBlank(message = "kfa code cannot be blank")
  private String kfaCode;
  @NotNull(message = "dose per unit cannot be blank")
  private Integer dosePerUnit;
}
