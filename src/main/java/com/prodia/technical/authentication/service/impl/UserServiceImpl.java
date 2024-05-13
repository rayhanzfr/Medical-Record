package com.prodia.technical.authentication.service.impl;

import com.prodia.technical.authentication.model.UserPrincipal;
import com.prodia.technical.authentication.model.request.CreateUserRequest;
import com.prodia.technical.authentication.model.request.ResetUserPasswordLinkRequest;
import com.prodia.technical.authentication.model.request.UpdateUserPasswordRequest;
import com.prodia.technical.authentication.model.request.UpdateUserRequest;
import com.prodia.technical.authentication.persistence.entity.RoleType;
import com.prodia.technical.authentication.persistence.entity.User;
import com.prodia.technical.authentication.persistence.repository.UserRepository;
import com.prodia.technical.authentication.service.UserService;
import com.prodia.technical.common.validation.ValidationHelper;
import com.prodia.technical.generalsetting.common.helper.UserConfigurationHelper;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  @Autowired
  private Environment environment;

  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final ValidationHelper validationHelper;
  private final UserConfigurationHelper userConfigurationHelper;

  private static final String NOT_FOUND = " not found.";


  @Override
  public User getEntityById(String id) {
    return repository.findById(id).orElseThrow(()->
        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Candidate address is not exist.")
    );
  }

  @Override
  @Transactional
  public User add(CreateUserRequest createUserRequest) {
    validationHelper.validate(createUserRequest);
    validateBkNotExist(createUserRequest.getUsername(), createUserRequest.getEmail());

    User user = new User();
    BeanUtils.copyProperties(createUserRequest, user);
    user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
    user.setIsActive(true);
    return repository.saveAndFlush(user);
  }

  @Override
  @Transactional
  public void edit(UpdateUserRequest updateUserRequest) {
    validationHelper.validate(updateUserRequest);
    User user = getEntityById(updateUserRequest.getId());
    validateBkNotChange(user, updateUserRequest);
    validateNonBk(updateUserRequest.getId(), updateUserRequest.getEmail());
    BeanUtils.copyProperties(updateUserRequest, user, "password");
    user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
    /* Commented for future password decryption from Front End */
    // user.setPassword(LoginEncryption.decrypt(userAddEditDto.getPassword()));

    repository.saveAndFlush(user);
  }

  @Override
  @Transactional
  public void delete(String id) {
    User user = getEntityById(id);
    repository.delete(user);
  }

  private void validateBkNotExist(String username, String email) {
    try {
      if (repository.existsByUsername(username)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate username.");
      }
      if (repository.existsByEmail(email)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate email.");
      }
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  private void validateBkNotChange(User user, UpdateUserRequest updateUserRequest) {
    if (!user.getUsername().equalsIgnoreCase(updateUserRequest.getUsername())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be changed.");
    }
  }

  private void validateNonBk(String id, String email) {
    if (repository.existsByEmailAndIdNot(email, id)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Email already in use by another user");
    }
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    User user = repository.findByUsername(username)
        .or(() -> repository.findByEmail(username)).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist."));
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(RoleType.ROLE_DEVELOPER.toString()));
    return UserPrincipal.builder().user(user).username(user.getUsername())
        .password(user.getPassword()).authorities(authorities).build();
  }

  @Override
  public UserDetailsService userDetailsService() {
    return new UserDetailsService() {
      @Override
      public UserDetails loadUserByUsername(String username) {
        return loadByUsername(username);
      }
    };
  }

  @Override
  public boolean existsByUsernameAndPassword(String username, String password) {
    if (repository.existsByUsername(username) || repository.existsByEmail(username)) {
      User user = null;

      User userByUsername = getEntityByUsername(username);
      User userByEmail = getEntityByEmail(username);
      if (userByUsername != null) {
        user = userByUsername;
      } else if (userByEmail != null) {
        user = getEntityByEmail(username);
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists.");
      }

      if (!passwordEncoder.matches(password, user.getPassword())) {
        repository.saveAndFlush(user);
        return false;
      } else {
        repository.saveAndFlush(user);
        return true;
      }
    } else {
      return false;
    }
  }

  @Override
  public User getEntityByUsername(String username) {
    return repository.findByUsername(username).orElse(null);
  }

  @Override
  public User getEntityByEmail(String email) {
    return repository.findByEmail(email).orElse(null);
  }

  private UserDetails loadByUsername(String username) {
    User user = repository.findByUsername(username)
        .or(() -> repository.findByEmail(username)).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist."));

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(RoleType.ROLE_DEVELOPER.toString()));
    return UserPrincipal.builder().user(user).username(user.getUsername())
        .password(user.getPassword()).authorities(authorities).build();
  }

  @Transactional
  @Override
  public void saveAndFlush(User entity) {
    repository.saveAndFlush(entity);
  }

  @Override
  public User sendResetPasswordLink(ResetUserPasswordLinkRequest resetPasswordRequest) {
    return repository.findByEmail(resetPasswordRequest.getEmail()).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist."));
  }

  @Override
  public String getUri() {
    return environment.getProperty("prodia.confirmation-email");
  }

  @Override
  public void saveVerificationToken(String token, User createdUser, String url) {

  }

  @Override
  public void resetPassword(UpdateUserPasswordRequest updateUserPassword) {

  }
}