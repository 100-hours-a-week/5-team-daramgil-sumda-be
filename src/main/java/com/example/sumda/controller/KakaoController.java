package com.example.sumda.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.sumda.DTO.KakaoUserInfoResponseDto;
import com.example.sumda.service.KakaoService;
import com.example.sumda.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kakao")
public class KakaoController {
    private final KakaoService kakaoService;
    private final UserRepository userRepository;

    // 카카오 로그인 콜백
    @GetMapping("/callback")
    public ResponseEntity<Map<String, Object>> callback(@RequestParam("code") String code, HttpSession session, HttpServletResponse response) {
        try {
            String accessToken = kakaoService.getAccessTokenFromKakao(code);
            KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);
            System.out.println(accessToken);

            // 세션에 사용자 정보 저장
            session.setAttribute("email", userInfo.getKakaoAccount().getEmail());
            session.setAttribute("nickname", userInfo.getKakaoAccount().getProfile().getNickName());
            session.setAttribute("profileImageUrl", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());
            session.setAttribute("accessToken", accessToken);

            // 클라이언트 측에 쿠키 설정 (optional)
            Cookie sessionCookie = new Cookie("sessionId", session.getId());
            sessionCookie.setHttpOnly(true);
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);

            // 클라이언트 측에 액세스 토큰을 쿠키로 설정 (optional)
            Cookie tokenCookie = new Cookie("accessToken", accessToken);
            tokenCookie.setHttpOnly(true);
            tokenCookie.setPath("/");
            response.addCookie(tokenCookie);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("accessToken", accessToken);
            responseBody.put("userInfo", userInfo);

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            log.error("Error during Kakao login", e);
            return ResponseEntity.status(500).body(Map.of("error", "Kakao login failed."));
        }
    }

    // 세션 정보 가져오기
    @GetMapping("/get-session")
    public ResponseEntity<Map<String, Object>> getSession(HttpSession session) {
        String email = (String) session.getAttribute("email");
        String nickname = (String) session.getAttribute("nickname");
        String profileImageUrl = (String) session.getAttribute("profileImageUrl");

        if (email != null) {
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("email", email);
            sessionData.put("nickname", nickname);
            sessionData.put("profileImageUrl", profileImageUrl);
            return ResponseEntity.ok(sessionData);
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "No session found."));
        }
    }

    // 로그아웃 (세션 무효화 및 쿠키 삭제)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session, HttpServletResponse response) {
        try {
            // 세션 무효화
            session.invalidate();

            // 클라이언트 측 쿠키 삭제
            Cookie sessionCookie = new Cookie("sessionId", null);
            sessionCookie.setMaxAge(0);
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);

            Cookie tokenCookie = new Cookie("accessToken", null);
            tokenCookie.setMaxAge(0);
            tokenCookie.setPath("/");
            response.addCookie(tokenCookie);

            return ResponseEntity.ok("Logged out from Kakao successfully.");
        } catch (Exception e) {
            log.error("Error during Kakao logout", e);
            return ResponseEntity.status(500).body("Logout failed.");
        }
    }

    // 회원 탈퇴 (카카오 연결 끊기)
    @PostMapping("/unlink")
    public ResponseEntity<String> unlink(HttpSession session, HttpServletResponse response) {
        try {
            String accessToken = (String) session.getAttribute("accessToken");
            if (accessToken == null) {
                return ResponseEntity.status(401).body("No access token found in session.");
            }

            // 카카오 계정 연결 해제 요청
            kakaoService.unlinkKakaoAccount(accessToken);

            // 세션 무효화 및 쿠키 삭제
            session.invalidate();

            Cookie sessionCookie = new Cookie("sessionId", null);
            sessionCookie.setMaxAge(0);
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);

            Cookie tokenCookie = new Cookie("accessToken", null);
            tokenCookie.setMaxAge(0);
            tokenCookie.setPath("/");
            response.addCookie(tokenCookie);

            return ResponseEntity.ok("Unlinked Kakao account successfully.");
        } catch (Exception e) {
            log.error("Error during Kakao unlink", e);
            return ResponseEntity.status(500).body("Unlink failed.");
        }
    }
}