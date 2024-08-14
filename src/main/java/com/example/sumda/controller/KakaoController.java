package com.example.sumda.controller;

import com.example.sumda.service.KakaoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

    public KakaoController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String accessToken) {
        kakaoService.logoutFromKakao(accessToken.replace("Bearer ", ""));
        return "Logged out from Kakao successfully.";
    }

    @PostMapping("/unlink")
    public String unlink(@RequestHeader("Authorization") String accessToken) {
        kakaoService.unlinkKakaoAccount(accessToken.replace("Bearer ", ""));
        return "Unlinked Kakao account successfully.";
    }
}