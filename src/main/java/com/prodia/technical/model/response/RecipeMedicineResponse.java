package com.prodia.technical.model.response;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeMedicineResponse {

  private String id;
  private Long version;
  private ZonedDateTime updatedAt;
  private String updatedBy;

  private String medicalRecordId;
  private String name;
  private String kfaCode;
  private Integer dosePerUnit;

}
