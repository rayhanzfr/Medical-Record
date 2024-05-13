package com.prodia.technical.model.request.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedicalRecordRequest {

  @Valid
  private CreatePatientRequest patient;

  @NotBlank(message = "recommendation medic cannot be null")
  private String recommendationMedic;

  @Valid
  private List<CreateDiagnoseRequest> diagnoses;
  @Valid
  private List<CreateRecipeMedicineRequest> recipeMedicines;
}
