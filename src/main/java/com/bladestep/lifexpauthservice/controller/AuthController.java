package com.bladestep.lifexpauthservice.controller;

import com.bladestep.lifexpauthservice.dto.LoginRequestDto;
import com.bladestep.lifexpauthservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public Mono<String> login(@RequestBody LoginRequestDto request) {
        return jwtService.generateToken(request.getEmail(), request.getRawPassword()).flatMap(Mono::just);
    }
}