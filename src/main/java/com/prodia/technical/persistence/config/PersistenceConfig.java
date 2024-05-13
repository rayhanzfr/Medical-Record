package com.prodia.technical.persistence.config;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(
    dateTimeProviderRef = "dateTimeProvider",
    auditorAwareRef = "auditorProvider"
)
public class PersistenceConfig {

  @Bean
  AuditorAware<String> auditorProvider() {
    return new AuditorAwareImpl();
  }

  @Bean
  public DateTimeProvider dateTimeProvider() {
    return () -> Optional.of(ZonedDateTime.now());
  }

  @Bean
  @Order(0)
  public FluentConfiguration coreFlywayMigrationConfig() {
    return Flyway.configure()
        .table("flyway_history")
        .locations("classpath:db/migration");
  }

  @Bean
  public FlywayMigrationStrategy multiModuleFlywayMigrationStrategy(
      List<FluentConfiguration> fluentConfigurations) {
    return flyway -> fluentConfigurations.forEach(
        migration -> migration.dataSource(flyway.getConfiguration().getDataSource())
            .schemas("public")
            .baselineOnMigrate(true)
            .load()
            .migrate());
  }
}
