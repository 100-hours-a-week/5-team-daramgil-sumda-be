package com.example.sumda.controller;

import com.example.sumda.dto.ai.request.WeatherAirDetailRequestDto;
import com.example.sumda.dto.ai.request.WeatherAndAirRequestDto;
import com.example.sumda.dto.ai.request.WeatherRequestDto;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.service.AiService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
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
}
