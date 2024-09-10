package com.example.sumda.jwt;

import com.example.sumda.dto.auth.CustomOAuth2User;
import com.example.sumda.entity.RefreshToken;
import com.example.sumda.entity.User;
import com.example.sumda.repository.RefreshTokenRepository;
import com.example.sumda.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${jwt.redirect}")
    private String REDIRECT_URI;


    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME; // 리프레쉬 토큰 유효기간

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        Long userId = customUserDetails.getId();
        String username = customUserDetails.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        User existUser = userRepository.findById(userId).orElse(null);

        User user;

        if(existUser == null) {
            log.info("신규 유저");

            user = User.createUser(userId, username, "email");

            userRepository.save(user);
        } else {
            log.info("기존 유저");
            refreshTokenRepository.deleteById(existUser.getId());
            user = existUser;
        }

        // 리프레쉬 토큰 발급 후 저장
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), REFRESH_TOKEN_EXPIRATION_TIME);

        RefreshToken newRefreshToken = RefreshToken.createRefreshToken(user.getId(), refreshToken);

        refreshTokenRepository.save(newRefreshToken);

        // 쿠키 생성
        Cookie cookie = createCookie("refresh_token", refreshToken);
        response.addCookie(cookie);

        // 리프레쉬 토큰 담아 리다이렉트
        response.sendRedirect(REDIRECT_URI);
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