package com.example.sumda.dto.ai.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WeatherAndAirReviewResponseDto implements Serializable {
    private String airQualityComment;         // 대기 질에 대한 코멘트
    private String weatherComment;            // 날씨에 대한 코멘트
    private String actionRecommendation;      // 민감군을 위한 행동 권장 사항

    // 정적 팩토리 메서드
    public static WeatherAndAirReviewResponseDto of(String airQualityComment, String weatherComment, String actionRecommendation) {
        return new WeatherAndAirReviewResponseDto(airQualityComment, weatherComment, actionRecommendation);
    }
}
