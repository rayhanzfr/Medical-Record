package com.prodia.technical.authentication.helper;

import com.prodia.technical.authentication.model.UserPrincipal;
import com.prodia.technical.authentication.persistence.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SessionHelper {

  public static User getLoginUser() {
    return ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .getUser();
  }

  public static UserPrincipal getUserPrincipal() {
    return ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
  }
}