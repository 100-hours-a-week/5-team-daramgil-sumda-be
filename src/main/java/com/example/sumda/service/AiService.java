package com.example.sumda.service;

import com.example.sumda.constans.AirQualityConstants;
import com.example.sumda.dto.ai.request.WeatherAirDetailRequestDto;
import com.example.sumda.dto.ai.request.WeatherAndAirRequestDto;
import com.example.sumda.dto.ai.request.WeatherRequestDto;
import com.example.sumda.dto.ai.response.ActivityReasonResponseDto;
import com.example.sumda.dto.ai.response.ClothesReasonResponseDto;
import com.example.sumda.dto.ai.response.WeatherAndAirReviewResponseDto;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class AiService {

    private final ChatClient chatClient;
    private final RedisService redisService;
    private final LocationService locationService;

    public WeatherAndAirReviewResponseDto getSummaryAi(WeatherAndAirRequestDto dto) {

        try {
            String locationName = locationService.getLocationName(dto.getLocationId());

            String locationHash = redisService.getLocationHash(locationName);



            String combinedAirInfo = String.format("민감군 여부: %s, 대기환경지수 값: %d, 대기환경지수 등급: %d",
                    dto.getSensitiveGroup(), dto.getKhaiValue(), dto.getKhaiGrade());
            String oneLineReview = chatClient.prompt()
                    .system(sp -> sp.param("text", AirQualityConstants.AIR_RESPONSE_PROMPT))
                    .user(combinedAirInfo)
                    .call()
                    .content();

            String review = chatClient.prompt()
                    .system(sp -> sp.param("text", AirQualityConstants.REVIEW_RESPONSE_PROMPT))
                    .user(dto.toString())
                    .call()
                    .content();

            String combinedWeatherInfo = String.format("날씨 여부 : %s, 현재 온도 : %.1f", dto.getWeatherType(), dto.getCurrentTemperature());
            String oneLineWeather = chatClient.prompt()
                    .system(sp -> sp.param("text", AirQualityConstants.WEATHER_RESPONSE_PROMPT))
                    .user(combinedWeatherInfo)
                    .call()
                    .content();



            return WeatherAndAirReviewResponseDto.of(oneLineReview, oneLineWeather, review);
        } catch(Exception e) {
            log.error("AI 서비스 호출 중 오류가 발생했습니다.", e);
            throw new CustomException(ErrorCode.AI_ERROR);
        }
    }

    public List<ActivityReasonResponseDto> getActivityRecommend(WeatherAirDetailRequestDto dto) {

        try {
            List<ActivityReasonResponseDto> recommends = chatClient.prompt()
                    .system(sp -> sp.param("text", AirQualityConstants.ACTIVITY_RESPONSE_PROMPT))
                    .user(dto.toString())
                    .call()
                    .entity(new ParameterizedTypeReference<List<ActivityReasonResponseDto>>() {
                    });

            return recommends;
        } catch (Exception e) {
            log.error("AI 서비스 호출 중 오류가 발생했습니다.", e);
            throw new CustomException(ErrorCode.AI_ERROR);
        }
    }

    public List<ClothesReasonResponseDto> getClothesRecommend(WeatherRequestDto dto) {

        try {
            List<ClothesReasonResponseDto> recommends = chatClient.prompt()
                    .system(sp -> sp.param("text", AirQualityConstants.CLOTHES_RESPONSE_PROMPT))
                    .user(dto.toString())
                    .call()
                    .entity(new ParameterizedTypeReference<List<ClothesReasonResponseDto>>() {
                    });

            return recommends;
        } catch (Exception e) {
            log.error("AI 서비스 호출 중 오류가 발생했습니다.", e);
            throw new CustomException(ErrorCode.AI_ERROR);
        }
    }
}
