package com.prodia.technical.authentication.verification.listener;

import com.prodia.technical.authentication.event.ResetPasswordEvent;
import com.prodia.technical.authentication.persistence.entity.User;
import com.prodia.technical.authentication.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResetPasswordEventListener implements ApplicationListener<ResetPasswordEvent> {

  private final UserService userService;

  private User createdUser;

  @Override
  public void onApplicationEvent(ResetPasswordEvent event) {
    createdUser = event.getUser();

    String verificationToken = UUID.randomUUID().toString();
    String url = userService.getUri() + "reset-password?token=" + verificationToken;
    String deepLink = "https://prodidev://dev-prodigital.prodia.com/reset-password?token="+ verificationToken;
    userService.saveVerificationToken(verificationToken, createdUser, url);
    sendResetPasswordEmail(url, deepLink);
  }

  public void sendResetPasswordEmail(String url, String deepLink){
//    ResetPasswordEmail.builder().emailService(emailService).createdJobPortalUser(createdUser).url(url).deepLink(deepLink).build().send();
  }

}