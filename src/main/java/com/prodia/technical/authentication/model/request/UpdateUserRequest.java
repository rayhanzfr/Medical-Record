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
public class UpdateUserRequest extends CreateUserRequest {

  @NotBlank(message = "Id cannot be empty.")
  private String id;

  @NotBlank(message = "Password cannot be empty.")
  private String password;

}