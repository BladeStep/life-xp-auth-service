package com.bladestep.lifexpauthservice.controller;

import static org.mockito.ArgumentMatchers.eq;

import com.bladestep.lifexpauthservice.config.SecurityConfig;
import com.bladestep.lifexpauthservice.dto.LoginRequestDto;
import com.bladestep.lifexpauthservice.service.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private JwtService jwtService;

    @Test
    void shouldReturnTokenOnValidLogin() {
        String email = "test@example.com";
        String password = "password123";
        String token = "jwt-token-example";

        Mockito.when(jwtService.generateToken(eq(email), eq(password)))
                .thenReturn(Mono.just(token));

        LoginRequestDto requestDto = new LoginRequestDto(email, password);


        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(token);
    }

    @Test
    void shouldReturnUnauthorizedOnAuthError() {
        String email = "wrong@example.com";
        String password = "wrongpass";

        Mockito.when(jwtService.generateToken(eq(email), eq(password)))
                .thenReturn(Mono.error(new RuntimeException("Invalid credentials")));

        LoginRequestDto requestDto = new LoginRequestDto(email, password);

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}