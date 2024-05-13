package com.prodia.technical.authentication.model;

import com.prodia.technical.authentication.model.response.JwtAuthenticationResponse;
import com.prodia.technical.authentication.persistence.entity.User;
import java.util.Collection;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
public class UserPrincipal implements UserDetails {

  private static final long serialVersionUID = -411400231909106259L;
  private String userAgent;
  private JwtAuthenticationResponse jwtAuthenticationResponse;
  private String ipAddress;
  private User user;
  private String username;
  private String password;

  private Collection<? extends GrantedAuthority> authorities;
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
