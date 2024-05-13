package com.prodia.technical.authentication.event;

import com.prodia.technical.authentication.persistence.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ResetPasswordEvent extends ApplicationEvent {

  private User user;


  public ResetPasswordEvent(User user) {
    super(user);
    this.user = user;
  }

}
