package com.example.sumda.service;

import com.example.sumda.dto.token.AccessTokenResponseDto;
import com.example.sumda.entity.RefreshToken;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.jwt.JWTUtil;
import com.example.sumda.repository.RefreshTokenRepository;
import com.example.sumda.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenService {

    private final UserRepository userRepository;
    @Value("${jwt.access-token.expiration-time}")
    private Long ACCESS_TOKEN_EXPIRATION_TIME; // 액세스 토큰 유효기간


    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    public AccessTokenResponseDto reissueAccessToken(String refreshToken) {

        String userId = jwtUtil.getUserIdFromToken(refreshToken);

        // 저장된 리프레시 토큰 조회
        RefreshToken existRefreshToken = refreshTokenRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        // 액세스 토큰을 저장할 변수
        String accessToken = null;


        if (!existRefreshToken.getToken().equals(refreshToken)) {
            log.error("리프레시 토큰 불일치");
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        accessToken = jwtUtil.generateAccessToken(Long.parseLong(userId), ACCESS_TOKEN_EXPIRATION_TIME);

        return AccessTokenResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }

    public void logout(Long userId, String refreshToken) {

        String tokenUserId = jwtUtil.getUserIdFromToken(refreshToken);

        // 주어진 userId와 refreshToken에서 추출한 userId 비교
        if (!userId.toString().equals(tokenUserId)) {
            // userId가 일치하지 않을 경우 예외 발생
            log.error("userId 불일치");
            throw new CustomException(ErrorCode.MISMATCH_USER_INFO);
        }

        refreshTokenRepository.deleteById(userId);
    }
}
