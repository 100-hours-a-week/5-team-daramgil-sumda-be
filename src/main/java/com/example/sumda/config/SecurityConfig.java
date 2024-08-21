//package com.example.sumda.config;
//
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .antMatchers("/login", "/register").permitAll()  // 로그인 및 등록 페이지는 인증 필요 없음
//                .anyRequest().authenticated()  // 다른 모든 요청은 인증 필요
//                .and()
//                .formLogin()
//                .loginPage("/login")  // 커스텀 로그인 페이지 설정
//                .permitAll()
//                .and()
//                .logout()
//                .permitAll();
//
//        // 세션 관리 추가
//        http
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // 세션이 필요할 때만 생성
//                .maximumSessions(1);  // 최대 1개의 세션 허용 (사용자당)
//    }
//}
