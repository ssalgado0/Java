package edu.uoc.epcsd.gateway;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GatewayApplication {

  static void main(String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
  }

}
