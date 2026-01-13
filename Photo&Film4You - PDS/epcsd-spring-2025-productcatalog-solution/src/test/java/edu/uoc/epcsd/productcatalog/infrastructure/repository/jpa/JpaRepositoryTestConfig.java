package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@TestConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories
@ComponentScan(basePackages = "edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa")
public class JpaRepositoryTestConfig {

}
