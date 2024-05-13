package com.prodia.technical.authentication.service.impl;

import com.prodia.technical.authentication.helper.LoginEncryption;
import com.prodia.technical.authentication.helper.SessionHelper;
import com.prodia.technical.authentication.model.request.AuthRequest;
import com.prodia.technical.authentication.model.request.CreateUserRequest;
import com.prodia.technical.authentication.model.response.JwtAuthenticationResponse;
import com.prodia.technical.authentication.persistence.entity.User;
import com.prodia.technical.authentication.service.AuthenticationService;
import com.prodia.technical.authentication.service.JwtService;
import com.prodia.technical.authentication.service.UserService;
import com.prodia.technical.common.validation.ValidationHelper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  @Setter(onMethod_ = @Autowired, onParam_ = @Lazy)
  private UserService userService;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final ValidationHelper validationHelper;


  @Override
  public JwtAuthenticationResponse login(AuthRequest loginRequest) throws AuthenticationException {
    if (!userService.existsByUsernameAndPassword(loginRequest.getUsername(),
        loginRequest.getPassword())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong username/password!");
    }
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
          loginRequest.getUsername(), loginRequest.getPassword()));
    var userPrinciple =
        userService.userDetailsService().loadUserByUsername(loginRequest.getUsername());
    System.out.println("User agent :: " + SessionHelper.getUserPrincipal().getUserAgent());
    String token =
        jwtService.generateToken(userPrinciple, SessionHelper.getUserPrincipal().getUserAgent());
    User user = null;
    User userByUsername = userService.getEntityByUsername(loginRequest.getUsername());
    User userByEmail = userService.getEntityByEmail(loginRequest.getUsername());
    if (userByUsername != null) {
      user = userByUsername;
    } else if (userByEmail != null) {
      user = userByEmail;
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists.");
    }
    return JwtAuthenticationResponse.builder().token(token).username(user.getUsername()).userEmail(user.getEmail()).build();
  }

  @Override
  public JwtAuthenticationResponse secureLogin(AuthRequest loginRequest) {
    loginRequest.setPassword(LoginEncryption.decrypt(loginRequest.getPassword()));
    return login(loginRequest);
  }

  @Override
  public void register(CreateUserRequest request) {
    userService.add(request);
  }
}
