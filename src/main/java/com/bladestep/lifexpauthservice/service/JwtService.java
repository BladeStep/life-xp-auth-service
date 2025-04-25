package com.bladestep.lifexpauthservice.service;

import com.bladestep.lifexpauthservice.client.UserClient;
import com.bladestep.lifexpauthservice.domain.UserRole;
import com.bladestep.lifexpauthservice.exception.UserNotFoundException;
import com.bladestep.lifexpauthservice.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final UserClient userClient;
    private final JwtProvider jwtProvider;

    public Mono<String> generateToken(String email, String password) {
        return userClient.findByCredentialsReactive(email, password)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found for email: " + email)))
                .flatMap(user -> {
                    Set<String> roles = user.getRoles().stream()
                            .map(UserRole::name)
                            .collect(Collectors.toSet());

                    String token = jwtProvider.createToken(user.getName(), roles);
                    return Mono.just(token);
                });
    }
}