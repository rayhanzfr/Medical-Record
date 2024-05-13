package com.prodia.technical.logging.config;

import com.prodia.technical.logging.RestApiLoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LoggingWebConfig {

  private final ApplicationEventPublisher applicationEventPublisher;

  @Bean
  public FilterRegistrationBean<RestApiLoggingFilter> apiLoggingFilterFilterRegistrationBean() {
    var registrationBean = new FilterRegistrationBean<RestApiLoggingFilter>();
    registrationBean.setFilter(new RestApiLoggingFilter(applicationEventPublisher));
    registrationBean.addUrlPatterns("/api/*");
    return registrationBean;
  }

}
