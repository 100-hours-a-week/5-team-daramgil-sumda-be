package com.example.sumda.service;

import com.example.sumda.dto.token.AccessTokenResponseDto;
import com.example.sumda.entity.RefreshToken;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.jwt.JWTUtil;
import com.example.sumda.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    @Value("${jwt.access-token.expiration-time}")
    private Long ACCESS_TOKEN_EXPIRATION_TIME; // 액세스 토큰 유효기간


    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    public AccessTokenResponseDto reissueAccessToken(String refreshToken) {

        String userId = jwtUtil.getUserIdFromToken(refreshToken);

        RefreshToken existRefreshToken = refreshTokenRepository.findById(Long.parseLong(userId))
                .orElse(null);
        System.out.println("existRefreshToken: " + existRefreshToken);
        String accessToken = null;


        if(!existRefreshToken.getToken().equals(refreshToken) || jwtUtil.isTokenExpired(refreshToken)) {
            // 리프레쉬 토큰이 다르거나, 만료된 경우
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } else {
            // 액세스 토큰 재발급
            accessToken = jwtUtil.generateAccessToken(Long.parseLong(userId), ACCESS_TOKEN_EXPIRATION_TIME);
        }

        return AccessTokenResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
