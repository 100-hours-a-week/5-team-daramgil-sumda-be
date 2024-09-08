package com.example.sumda.controller;

import com.example.sumda.dto.game.request.GameResultRequestDto;
import com.example.sumda.dto.game.response.GameResultResponseDto;
import com.example.sumda.service.GameService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/game")
public class GameController {

    private final GameService gameService;

    @PostMapping("/result")
    public ResponseEntity<?> gameResult(@RequestBody GameResultRequestDto gameResultRequestDto){

        int score = gameResultRequestDto.getScore(); // 게임 점수
        // TODO: jwt 적용 후 변경
        Long userId = 1L;
        GameResultResponseDto gameResultResponseDto = gameService.gameResult(userId, gameResultRequestDto);

        if (score<10) {
            return ResponseUtils.createResponse(HttpStatus.OK, "적립된 도토리가 없습니다.", gameResultResponseDto);
        }
        return ResponseUtils.createResponse(HttpStatus.OK,"떨어지는 도토리를 받아받아 게임 도토리 적립 완료", gameResultResponseDto);
    }
}
