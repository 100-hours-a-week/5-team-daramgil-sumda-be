package com.example.sumda.jwt;

import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private SecretKey getSigningKey() {
        byte [] keyBytes = Decoders.BASE64.decode(this.SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

   public String generateAccessToken(Long userId, Long expirationMillis) {
        log.info("액세스 토큰 발행 성공");

        return Jwts.builder()
                .claim("userId", userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(this.getSigningKey())
                .compact();
   }

   public String generateRefreshToken(Long userId, Long expirationMillis) {
        log.info("리프레시 토큰 발행 성공");

        return Jwts.builder()
                .claim("userId", userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(this.getSigningKey())
                .compact();
   }

   // 응답 헤더에서 액세스 토큰을 반환하는 메서드
    public String getUserIdFromToken(String token) {
        try {
            String userId = Jwts.parser()
                    .verifyWith(this.getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("userId",String.class);

            log.info("유저 id 반환");
            return userId;
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException |
                 io.jsonwebtoken.security.SignatureException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.UNSUPPORTED_TOKEN);
        }
    }
}