package com.example.sumda.controller;

import com.example.sumda.dto.auth.CustomOAuth2User;
import com.example.sumda.dto.game.request.GameResultRequestDto;
import com.example.sumda.dto.game.response.GameResultResponseDto;
import com.example.sumda.dto.game.response.HighestScoreResponseDto;
import com.example.sumda.service.GameService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/game")
public class GameController {

    private final GameService gameService;

    // 게임 결과 처리
    @PostMapping("/result")
    public ResponseEntity<?> gameResult(
            @AuthenticationPrincipal CustomOAuth2User oAuth2User,
            @RequestBody GameResultRequestDto gameResultRequestDto) {

        Long userId = oAuth2User.getId(); // 인증된 사용자로부터 userId 추출
        int score = gameResultRequestDto.getScore(); // 게임 점수
        GameResultResponseDto gameResultResponseDto = gameService.gameResult(userId, gameResultRequestDto);

        if (score < 10) {
            return ResponseUtils.createResponse(HttpStatus.OK, "적립된 도토리가 없습니다.", gameResultResponseDto);
        }
        return ResponseUtils.createResponse(HttpStatus.OK, "게임 결과 도토리 적립 완료", gameResultResponseDto);
    }

    // 유저의 가장 높은 점수 반환
    @GetMapping("/highest-score")
    public ResponseEntity<?> getHighestScore(
            @AuthenticationPrincipal CustomOAuth2User oAuth2User,
            @RequestParam Long gameTypeId) {

        Long userId = oAuth2User.getId(); // 인증된 사용자로부터 userId 추출
        HighestScoreResponseDto highestScore = gameService.highestScore(userId, gameTypeId);

        return ResponseUtils.createResponse(HttpStatus.OK, "유저의 가장 높은 게임 점수", highestScore);
    }
}
