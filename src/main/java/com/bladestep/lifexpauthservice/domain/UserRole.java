package com.bladestep.lifexpauthservice.domain;

public enum UserRole {

    USER("USER"),
    ADMIN("ADMIN"),
    MODERATOR("MODERATOR");

    private String value;

    UserRole(String value) {
        this.value = value;
    }
}