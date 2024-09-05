package com.example.sumda.dto.auth;

import lombok.Getter;

@Getter
public class UserDto {

    private String role;
    private String name;
    private String username;

    public UserDto(String role, String name, String username) {
        this.role = role;
        this.name = name;
        this.username = username;
    }
}