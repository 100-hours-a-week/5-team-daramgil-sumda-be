package com.example.sumda.controller;


import com.example.sumda.dto.auth.CustomOAuth2User;
import com.example.sumda.utils.ResponseUtils;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    //TODO: 추후에 변경 예정
    @GetMapping("/check")
    public ResponseEntity<?> checkLoginStatus(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        if (oAuth2User == null || oAuth2User.getName() == null) {
            return ResponseUtils.createResponse(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.", null);
        }
        return ResponseUtils.createResponse(HttpStatus.OK, "로그인 상태입니다.", null);
    }
}
