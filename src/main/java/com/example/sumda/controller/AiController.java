package com.example.sumda.controller;

import com.example.sumda.dto.ai.request.WeatherAirDetailRequestDto;
import com.example.sumda.dto.ai.request.WeatherAndAirRequestDto;
import com.example.sumda.dto.ai.request.WeatherRequestDto;
import com.example.sumda.dto.auth.CustomOAuth2User;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.service.AiService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

    private final AiService aiService;

    @GetMapping("/simple")
    public ResponseEntity<?> completion(@ModelAttribute WeatherAndAirRequestDto weatherAndAirRequestDto) {

        if (weatherAndAirRequestDto == null) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        return ResponseUtils.createResponse(HttpStatus.OK,"오늘 날씨 조언에 메시지 추가 완료",aiService.getSummaryAi(weatherAndAirRequestDto));

    }

    @PostMapping("/activity")
    public ResponseEntity<?> activity(@RequestBody WeatherAirDetailRequestDto weatherAirDetailRequestDto) {

        if(weatherAirDetailRequestDto == null) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        return ResponseUtils.createResponse(HttpStatus.OK,"오늘 날씨에 어울리는 행동 추천 메시지 추가 완료",aiService.getActivityRecommend(weatherAirDetailRequestDto));
    }

    @GetMapping("/clothes")
    public ResponseEntity<?> clothes(@ModelAttribute WeatherRequestDto weatherRequestDto) {

        if(weatherRequestDto == null) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        return ResponseUtils.createResponse(HttpStatus.OK,"오늘 날씨에 어울리는 옷 추천 메시지 추가 완료", aiService.getClothesRecommend(weatherRequestDto));
    }
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestBody Map<String, Object> input) {
        try {
            // 사용자 입력 JSON 데이터를 파싱
            String question = (String) input.get("question");
            Map<String, Object> airQuality = (Map<String, Object>) input.get("air_quality");
            Map<String, Object> weather = (Map<String, Object>) input.get("weather");

            // AiService로 질문 처리 요청
            String response = aiService.handleChat(question, airQuality, weather);

            // JSON 형식의 응답 반환
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("answer", response);

            return ResponseUtils.createResponse(HttpStatus.OK, "질문에 대한 답변을 생성했습니다.", responseBody);
        } catch (Exception e) {
            log.error("Chat 요청 처리 중 오류가 발생했습니다.", e);
            throw new CustomException(ErrorCode.AI_ERROR);
        }
    }
}
