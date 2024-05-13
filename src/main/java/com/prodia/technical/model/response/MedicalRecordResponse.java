package com.prodia.technical.model.response;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordResponse {

  private String id;
  private Long version;
  private ZonedDateTime updatedAt;
  private String updatedBy;

  private PatientResponse patient;
  private String recommendationMedic;
  private List<DiagnoseResponse> diagnoses;
  private List<RecipeMedicineResponse> recipeMedicines;
}
