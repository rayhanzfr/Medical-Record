package com.prodia.technical.common.helper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanUtil implements ApplicationContextAware {

  private static ApplicationContext context;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    context = applicationContext;
  }

  public static <T> T getBean(Class<T> beanClass) {
    return context.getBean(beanClass);
  }

  public Object getBean(String bean) {
    return context.getBean(bean);
  }

  public static ApplicationContext getApplicationContext() {
    return context;
  }
}