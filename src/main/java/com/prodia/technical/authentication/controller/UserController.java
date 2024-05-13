package com.prodia.technical.authentication.controller;

import com.prodia.technical.authentication.event.ResetPasswordEvent;
import com.prodia.technical.authentication.model.request.ResetUserPasswordLinkRequest;
import com.prodia.technical.authentication.model.request.UpdateUserPasswordRequest;
import com.prodia.technical.authentication.persistence.entity.User;
import com.prodia.technical.authentication.service.UserService;
import com.prodia.technical.common.helper.ResponseHelper;
import com.prodia.technical.common.model.response.WebResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {

  private final UserService service;
  private final ApplicationEventPublisher publisher;
  @PostMapping("/users/send-reset-password-link")
  public ResponseEntity<WebResponse<String>> sendResetPassword(
      @RequestBody ResetUserPasswordLinkRequest paswordLinkRequest) {
    User user = service.sendResetPasswordLink(paswordLinkRequest);
    publisher.publishEvent(new ResetPasswordEvent(user));
    return ResponseEntity.ok(ResponseHelper.ok("Reset password link successfully send"));
  }

  @PutMapping("/users/reset-password")
  public ResponseEntity<WebResponse<String>> resetPassword(
      @RequestBody UpdateUserPasswordRequest updateUserPassword) {
    service.resetPassword(updateUserPassword);
    return ResponseEntity.ok(ResponseHelper.ok("Password reset successfully"));
  }
}
