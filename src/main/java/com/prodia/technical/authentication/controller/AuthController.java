package com.prodia.technical.authentication.controller;

import com.prodia.technical.authentication.model.request.AuthRequest;
import com.prodia.technical.authentication.model.request.CreateUserRequest;
import com.prodia.technical.authentication.model.response.JwtAuthenticationResponse;
import com.prodia.technical.authentication.service.AuthenticationService;
import com.prodia.technical.common.helper.ResponseHelper;
import com.prodia.technical.common.model.response.WebResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationService authenticationService;

  @PostMapping("/login")
  public ResponseEntity<WebResponse<JwtAuthenticationResponse>> authenticateUser(@Valid @RequestBody AuthRequest loginRequest) {
    return ResponseEntity.ok(ResponseHelper.ok(authenticationService.login(loginRequest)));
  }

  @PostMapping("/signup")
  public ResponseEntity<WebResponse<String>> registerUser(@Valid @RequestBody CreateUserRequest signUpRequest) {
    authenticationService.register(signUpRequest);
    return ResponseEntity.ok(ResponseHelper.ok("User registered successfully!"));
  }
}