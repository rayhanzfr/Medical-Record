package com.prodia.technical.authentication.service;

import com.prodia.technical.authentication.model.request.AuthRequest;
import com.prodia.technical.authentication.model.request.CreateUserRequest;
import com.prodia.technical.authentication.model.response.JwtAuthenticationResponse;

public interface AuthenticationService {
  
  JwtAuthenticationResponse login(AuthRequest loginRequest);

  void register(CreateUserRequest request);

  JwtAuthenticationResponse secureLogin(AuthRequest loginRequest);
}