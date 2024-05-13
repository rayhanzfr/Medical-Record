package com.prodia.technical.authentication.service;

import com.prodia.technical.authentication.model.request.CreateUserRequest;
import com.prodia.technical.authentication.model.request.ResetUserPasswordLinkRequest;
import com.prodia.technical.authentication.model.request.UpdateUserPasswordRequest;
import com.prodia.technical.authentication.model.request.UpdateUserRequest;
import com.prodia.technical.authentication.model.response.UserResponse;
import com.prodia.technical.authentication.persistence.entity.User;
import com.prodia.technical.common.model.request.PagingRequest;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

public interface UserService {

  User getEntityById(String id);
  User add(CreateUserRequest request);
  boolean existsByUsernameAndPassword(String username, String password);
  void edit(UpdateUserRequest request);
  void delete(String id);

  UserDetails loadUserByUsername(String username);
  UserDetailsService userDetailsService();
  void saveAndFlush(User entity);

  public User getEntityByUsername(String username);
  public User getEntityByEmail(String email);
  User sendResetPasswordLink(ResetUserPasswordLinkRequest forgotPasswordRequest);

  String getUri();
  void saveVerificationToken(String token, User createdUser, String url);
  void resetPassword(UpdateUserPasswordRequest updateUserPassword);
}
