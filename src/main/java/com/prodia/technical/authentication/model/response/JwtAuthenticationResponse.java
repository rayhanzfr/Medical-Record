package com.prodia.technical.authentication.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
  private String token;
  private String refreshToken;
  private String username;
  private String userEmail;
}