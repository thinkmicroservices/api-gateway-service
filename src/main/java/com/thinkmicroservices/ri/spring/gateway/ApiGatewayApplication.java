package com.thinkmicroservices.ri.spring.gateway;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ApiGatewayApplication {

    @Value("${configuration.source:DEFAULT}")
    String configSource;
    @Value("${spring.application.name:NOT-SET}")
    private String serviceName;

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);

    }

    @PostConstruct
    private void displayInfo() {
        log.info("Service-Name:{}, configuration.source={}", serviceName, configSource);
    }
}
