package com.prodia.technical.authentication.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class UserPrincipalAuthenticationToken extends AbstractAuthenticationToken {

  private static final long serialVersionUID = -386437028321436651L;

  private final UserPrincipal userPrinciple;

  public UserPrincipalAuthenticationToken(UserPrincipal userPrinciple) {
    super(userPrinciple.getAuthorities());
    this.userPrinciple = userPrinciple;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public UserPrincipal getPrincipal() {
    return userPrinciple;
  }

}
