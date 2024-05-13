package com.prodia.technical.persistence.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@TestConfiguration
@Import({PersistenceConfig.class})
@EnableJpaRepositories(basePackages = "com.prodia.technical")
@EntityScan(basePackages = "com.prodia.technical")
@EnableAsync
public class PersistenceTestConfig {

}
