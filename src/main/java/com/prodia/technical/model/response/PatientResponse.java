package com.prodia.technical.model.response;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {

  private String id;
  private Long version;
  private ZonedDateTime updatedAt;
  private String updatedBy;

  private String name;
  private LocalDate birthDate;
  private String email;
  private String phone;
}
