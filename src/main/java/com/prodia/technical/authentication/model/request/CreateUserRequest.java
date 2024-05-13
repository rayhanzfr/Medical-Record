package com.prodia.technical.authentication.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
  @NotBlank(message = "Username cannot be empty.")
  private String username;

  @NotBlank(message = "Email cannot be empty.")
  @Email
  private String email;

  @NotBlank(message = "Password cannot be empty. ")
  private String password;

  private Boolean isActive;

  @NotNull(message = "Effective begin cannot be null")
  private LocalDate effBegin;

  @NotNull(message = "Effective end cannot be null")
  private LocalDate effEnd;

  private String id;
}