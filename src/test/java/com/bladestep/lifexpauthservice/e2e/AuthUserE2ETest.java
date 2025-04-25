package com.bladestep.lifexpauthservice.e2e;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bladestep.lifexpauthservice.annotation.E2ETest;
import com.bladestep.lifexpauthservice.dto.LoginRequestDto;
import com.bladestepapp.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@E2ETest
class AuthUserE2ETest {

    @Autowired
    private WireMock wireMock;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    private WebClient testClient;

    @BeforeEach
    void setUp() {
        this.testClient = WebClient.create("http://localhost:" + port);
    }

    @Test
    void shouldReturnTokenDuringAuthUser() {

        String email = "user@test.com";
        String password = "password123";

        stubUserExists(email, password);

        LoginRequestDto request = new LoginRequestDto(email, password);

        Mono<String> response = testClient.post()
                .uri("/auth/login")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(Assertions::assertNotNull);

       response.block();
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() {

        String email = "wronguser@test.com";
        String password = "wrongpassword123";

        stubUserNotFound(email, password);

        LoginRequestDto request = new LoginRequestDto(email, password);

        Mono<ResponseEntity<String>> response = testClient.post()
                .uri("/auth/login")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(body -> {
                            assertEquals(HttpStatus.NOT_FOUND, clientResponse.statusCode());
                            assertEquals("User not found for email: " + email, body);
                            return Mono.empty();
                        }))
                .toEntity(String.class);

        response.block();
    }

    @SneakyThrows
    private void stubUserExists(String email, String password) {

        AuthUserResponseDto authUserResponseDto = new AuthUserResponseDto(UUID.randomUUID(), email);
        authUserResponseDto.setName("John Smith");
        authUserResponseDto.setRoles(List.of(UserRoleDto.USER, UserRoleDto.MODERATOR));

        MonoAuthUserResponseDto monoAuthUserResponseDto = new MonoAuthUserResponseDto();
        monoAuthUserResponseDto.setIsSuccess(true);
        monoAuthUserResponseDto.setData(authUserResponseDto);

        String jsonResponse = objectMapper.writeValueAsString(monoAuthUserResponseDto);

        wireMock.register(post(urlEqualTo("/api/user/by-credentials"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(matchingJsonPath("$.email", equalTo(email)))
                .withRequestBody(matchingJsonPath("$.password", equalTo(password)))
                .willReturn(okJson(jsonResponse))
        );
    }

    @SneakyThrows
    private void stubUserNotFound(String email, String password) {
        MonoAuthUserResponseDto monoAuthUserResponseDto = new MonoAuthUserResponseDto();
        monoAuthUserResponseDto.setIsSuccess(false);
        monoAuthUserResponseDto.setErrorMessage("Not Found");

        String jsonResponse = objectMapper.writeValueAsString(monoAuthUserResponseDto);

        wireMock.register(post(urlEqualTo("/api/user/by-credentials"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(matchingJsonPath("$.email", equalTo(email)))
                .withRequestBody(matchingJsonPath("$.password", equalTo(password)))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(jsonResponse)));
    }
}