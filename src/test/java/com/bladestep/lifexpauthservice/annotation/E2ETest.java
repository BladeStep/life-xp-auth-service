package com.bladestep.lifexpauthservice.annotation;

import com.bladestep.lifexpauthservice.config.WireMockTestConfig;
import com.bladestep.lifexpauthservice.extension.WireMockCleanupExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(
        initializers = WireMockTestConfig.class
)
@Import(WireMockTestConfig.class)
@ExtendWith({WireMockCleanupExtension.class})
public @interface E2ETest {
}