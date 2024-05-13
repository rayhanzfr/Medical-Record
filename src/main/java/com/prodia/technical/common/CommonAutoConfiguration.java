package com.prodia.technical.common;

import com.prodia.technical.common.properties.LoggingProperties;
import com.prodia.technical.common.properties.PagingProperties;
import com.prodia.technical.common.web.PagingRequestArgumentResolver;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties({
  PagingProperties.class,
  LoggingProperties.class,
})
@AllArgsConstructor
@EnableAsync
public class CommonAutoConfiguration implements WebMvcConfigurer {

  private final PagingProperties pagingProperties;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new PagingRequestArgumentResolver(pagingProperties));
  }

}