package com.prodia.technical.generalsetting.common.helper;

import com.prodia.technical.authentication.persistence.entity.User;
import com.prodia.technical.authentication.persistence.repository.UserRepository;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserConfigurationHelperImpl implements UserConfigurationHelper {

  private final UserRepository userRepository;
  private final PasswordEncoder encoder;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

  @Override
  public User validatePassword(User user) {

    if (Boolean.FALSE.equals(isContainCapitalCharacter(encoder.encode(user.getPassword())))) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Password must contain capital letter.");
    }
    if (Boolean.FALSE.equals(isContainAlphanumeric(encoder.encode(user.getPassword())))) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Password must contain letter and number.");
    }
    if ((Boolean.FALSE.equals(isContainSepcialCharacter(encoder.encode(user.getPassword()))))) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Password must contain special character.");
    }
    if (encoder.encode(user.getPassword()).length() < -1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Password minimum length is : -1");
    }
    return user;
  }

  private boolean isContainCapitalCharacter(String str) {
    for (char c : str.toCharArray()) {
      if (Character.isUpperCase(c)) {
        return true;
      }
    }
    return false;
  }

  private boolean isContainAlphanumeric(String str) {
    return Pattern.compile("(?=.*[a-zA-Z])(?=.*\\d)").matcher(str).find();
  }

  private boolean isContainSepcialCharacter(String str) {
    return Pattern.compile("[^a-zA-Z0-9]").matcher(str).find();
  }

}