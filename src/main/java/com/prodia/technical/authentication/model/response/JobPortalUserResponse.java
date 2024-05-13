package com.prodia.technical.authentication.model.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobPortalUserResponse {
  private String id;
  private String nik;
  private String candidateName;
  private String username;
  private LocalDate effectiveBegin;
  private LocalDate effectiveEnd;
  private String effectiveEndBy;
  private Boolean isActive;
  private Long version;
  private String email;
  private String status;
  private String candidateId;
}
