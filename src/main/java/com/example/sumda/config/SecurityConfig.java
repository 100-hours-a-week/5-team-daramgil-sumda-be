package com.example.sumda.config;

import com.example.sumda.exception.security.CustomAccessDeniedHandler;
import com.example.sumda.exception.security.CustomAuthenticationEntryPoint;
import com.example.sumda.jwt.CustomFailureHandler;
import com.example.sumda.jwt.CustomSuccessHandler;
import com.example.sumda.jwt.JWTFilter;
import com.example.sumda.jwt.JWTUtil;
import com.example.sumda.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.example.sumda.oauth.CustomOAuth2UserService;
import com.example.sumda.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final HttpCookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomFailureHandler customFailureHandler;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ) // 세션 사용하지 않음

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/img/**","/images/**").permitAll()  // 이미지 경로 허용
                        .requestMatchers("/**").permitAll()
                )
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .authorizationEndpoint(
                                authorizationEndpointConfig -> authorizationEndpointConfig.authorizationRequestRepository(
                                        cookieOAuth2AuthorizationRequestRepository))
                        .successHandler(customSuccessHandler)
                        .failureHandler(customFailureHandler)
                );

        http

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler()));


        //JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
