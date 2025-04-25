package com.bladestep.lifexpauthservice.dto;

import lombok.Value;

@Value
public class LoginRequestDto {

    String email;

    String rawPassword;
}