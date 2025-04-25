package com.bladestep.lifexpauthservice.client;

import com.bladestep.lifexpauthservice.client.model.UserResponseModel;
import com.bladestep.lifexpauthservice.domain.UserRole;
import com.bladestep.lifexpauthservice.exception.RemoteAuthFailureException;
import com.bladestep.lifexpauthservice.exception.UserNotFoundException;
import com.bladestep.lifexpauthservice.exception.UserServiceException;
import com.bladestep.lifexpauthservice.properties.UserServiceProperties;
import com.bladestepapp.model.AuthRequestDto;
import com.bladestepapp.model.MonoAuthUserResponseDto;
import com.bladestepapp.model.UserRoleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClient {

    private final WebClient userWebClient;
    private final UserServiceProperties userServiceProperties;

    public Mono<UserResponseModel> findByCredentialsReactive(String email, String password) {
        AuthRequestDto requestDto = new AuthRequestDto(email, password);

        log.debug("Sending authentication request for user: {}", email);

        return userWebClient.post()
                .uri(userServiceProperties.getUserPathByCredentials())
                .bodyValue(requestDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> handleErrorResponse(response, email))
                .bodyToMono(MonoAuthUserResponseDto.class)
                .flatMap(response -> {
                    if (!Boolean.TRUE.equals(response.getIsSuccess()) || response.getData() == null) {
                        log.warn("Authentication failed or user data is missing for email: {}", email);
                        return Mono.error(new RemoteAuthFailureException(response.getErrorMessage()));
                    }
                    return mapToUserModel(response);
                });
    }

    private Mono<UserResponseModel> mapToUserModel(MonoAuthUserResponseDto response) {
        Set<UserRole> mappedRoles = response.getData().getRoles().stream()
                .map(UserRoleDto::getValue)
                .map(String::toUpperCase)
                .flatMap(role -> {
                    try {
                        return Stream.of(UserRole.valueOf(role));
                    } catch (IllegalArgumentException e) {
                        log.warn("Unknown role received from user service: {}", role);
                        return Stream.empty(); // skip invalid roles
                    }
                })
                .collect(Collectors.toSet());

        return Mono.just(new UserResponseModel(
                response.getData().getName(),
                response.getData().getEmail(),
                mappedRoles
        ));
    }

    private Mono<? extends Throwable> handleErrorResponse(ClientResponse response, String email) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("Unknown error")
                .flatMap(errorBody -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        log.info("User not found during auth request: {}", email);
                        return Mono.empty();
                    }
                    log.error("User service error during auth for {}: [{}] {}", email, response.statusCode(), errorBody);
                    return Mono.error(new UserServiceException("User service error: " + errorBody, response.statusCode()));
                });
    }
}