package com.prodia.technical.authentication.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPasswordRequest {

  @NotBlank(message = "Password cannot be empty")
  private String password;

  @NotBlank(message = "Token cannot be empty. ")
  private String token;

}
