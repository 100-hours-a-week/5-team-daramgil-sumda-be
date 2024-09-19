package com.example.sumda.jwt;

import com.example.sumda.dto.auth.CustomOAuth2User;
import com.example.sumda.dto.auth.UserDto;
import com.example.sumda.entity.User;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더에서 토큰을 가져옴
        String authorizationHeader = request.getHeader("Authorization");

        // Authorization 헤더 검증
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 다음에 있는 토큰 값만 추출
        String token = authorizationHeader.substring(7);

        //토큰에서 username과 role 획득
        String userId;
        try {
            userId = jwtUtil.getUserIdFromToken(token);
        } catch(CustomException e) {
            log.warn("accessToken 검증 실패 : {}", e.getMessage());
            setErrorResponse(response, e.getErrorCode());
            return;
        }

        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        //userDTO를 생성하여 값 set
        UserDto userDTO = new UserDto(user.getId(),user.getUserName());

        //UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    // 필터에서 발생한 예외에 대한 응답을 설정
    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 기본적으로 401 상태 코드 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format("{\"errorCode\": \"%s\", \"message\": \"%s\"}",
                errorCode.name(), errorCode.getMessage());
        response.getWriter().write(jsonResponse);
    }
}