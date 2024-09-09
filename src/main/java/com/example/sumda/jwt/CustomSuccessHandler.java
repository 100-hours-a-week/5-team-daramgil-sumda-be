package com.example.sumda.jwt;

import com.example.sumda.dto.auth.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // JWT 토큰 생성
        String token = jwtUtil.createJwt(username, role, 60 * 60 * 6000L);

        // 클라이언트로 JWT 토큰 전달
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 토큰을 JSON 형태로 응답의 body에 작성
        String jsonResponse = "{\"Token\":\"" + token + "\"}";
        response.getWriter().write(jsonResponse);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}