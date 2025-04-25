package com.bladestep.lifexpauthservice.client.model;
import com.bladestep.lifexpauthservice.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserResponseModel {

    private String name;

    private String email;

    Set<UserRole> roles;
}