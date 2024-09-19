package com.example.sumda.controller;

import com.example.sumda.dto.auth.CustomOAuth2User;
import com.example.sumda.service.TokenService;
import com.example.sumda.utils.CookieUtils;
import com.example.sumda.utils.ResponseUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;

    //TODO: 추후에 변경 예정
    @GetMapping("/check")
    public ResponseEntity<?> checkLoginStatus(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        if (oAuth2User == null || oAuth2User.getName() == null) {
            return ResponseUtils.createResponse(HttpStatus.UNAUTHORIZED, "로그인 상태가 아닙니다.", null);
        }

        return ResponseUtils.createResponse(HttpStatus.OK, "로그인 상태입니다.", null);
    }

    @GetMapping("/reissue")
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request) {

        Optional<Cookie> refreshTokenCookie = CookieUtils.getCookie(request, "refresh_token");

        if (refreshTokenCookie.isPresent()) {
            String refreshToken = refreshTokenCookie.get().getValue();
            return ResponseUtils.createResponse(HttpStatus.OK, "액세스 토큰 재발급 성공", tokenService.reissueAccessToken(refreshToken));
        } else {
            return ResponseUtils.createResponse(HttpStatus.UNAUTHORIZED, "Refresh token not found.", null);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {

        Long userId = oAuth2User.getId();

        Optional<Cookie> refreshTokenCookie = CookieUtils.getCookie(request, "refresh_token");

        if(refreshTokenCookie.isPresent()) {
            tokenService.logout(userId,refreshTokenCookie.get().getValue());
            Cookie deleteCookie = new Cookie("refresh_token", null);
            deleteCookie.setMaxAge(0);
            deleteCookie.setPath("/");
            deleteCookie.setHttpOnly(true);
            response.addCookie(deleteCookie);
        } else {
            return ResponseUtils.createResponse(HttpStatus.UNAUTHORIZED, "Refresh token not found.", null);
        }

        return ResponseUtils.createResponse(HttpStatus.OK, "로그아웃 성공", null);
    }
}
