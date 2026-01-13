package edu.uoc.epcsd.digital.testutils;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@TestConfiguration
public class RestTemplateConfig {

  @Bean
  public TestRestTemplate testRestTemplate(RestTemplateBuilder builder) {
    RestTemplateBuilder patched = builder.requestFactory(
        HttpComponentsClientHttpRequestFactory::new);
    return new TestRestTemplate(patched);
  }
}
