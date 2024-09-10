package com.example.sumda.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "refresh_token", timeToLive = 60 * 60 * 24)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RefreshToken {

    @Id
    private Long userId; // 유저 ID

    private String token; // 실제 Refresh Token 값


    // RefreshToken 생성 메소드
    public static RefreshToken createRefreshToken(Long userId, String token) {
        return RefreshToken.builder()
                .userId(userId)
                .token(token)
                .build();
    }
}
