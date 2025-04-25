package com.bladestep.lifexpauthservice.config;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

@TestConfiguration
public class WireMockTestConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Container
    public static final GenericContainer<?> wireMockContainer =
            new GenericContainer<>("wiremock/wiremock:3.3.1")
                    .withExposedPorts(8080);

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        wireMockContainer.start();
        TestPropertyValues.of(
                "user-service.base-url=http://localhost:" + wireMockContainer.getMappedPort(8080),
                "user-service.user-path-by-credentials=/api/user/by-credentials"
        ).applyTo(context);
    }

    @Bean
    public WireMock wireMockClient() {
        return WireMock.create()
                .host("localhost")
                .port(wireMockContainer.getMappedPort(8080))
                .build();
    }
}