package com.example.sumda.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class UserDto {

    private Long userId;
    private String name;

    public static UserDto of(Long userId,  String name) {
        return new UserDto(userId, name);
    }
}