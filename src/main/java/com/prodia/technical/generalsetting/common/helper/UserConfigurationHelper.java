package com.prodia.technical.generalsetting.common.helper;

import com.prodia.technical.authentication.persistence.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserConfigurationHelper {

  User validatePassword(User user);

}