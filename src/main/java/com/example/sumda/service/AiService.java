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

    public WeatherAndAirReviewResponseDto getSummaryAi(WeatherAndAirRequestDto dto) {

        try {
            String combinedAirInfo = String.format("민감군 여부: %s, 대기환경지수 값: %d, 대기환경지수 등급: %d",
                    dto.getSensitiveGroup(), dto.getKhaiValue(), dto.getKhaiGrade());
            String oneLineReview = chatClient.prompt()
                    .system(sp -> sp.param("text", AirQualityConstants.CUTE_RESPONSE_PROMPT))
                    .user(combinedAirInfo)
                    .call()
                    .content();

            String review = chatClient.prompt()
                    .system(sp -> sp.param("text", "우리 서비스는 귀엽게 대답을 해줘야 돼." +
                            "<다음> 아래의 데이터를 학습하고 거기에 대해서 오늘의 대기질과 날씨의 대한 행동 요령을 추천 해줘." +
                            "<다음>"))
                    .user(dto.toString())
                    .call()
                    .content();

            String combinedWeatherInfo = String.format("날씨 여부 : %s, 현재 온도 : %.1f", dto.getWeatherType(), dto.getCurrentTemperature());
            String oneLineWeather = chatClient.prompt()
                    .system(sp -> sp.param("text", "우리 서비스는 귀엽게 대답을 해줘야 돼." +
                            "<다음> 아래의 데이터를 학습하고 거기에 대해서 오늘의 날씨를을 딱 한줄로 평가해줘." +
                            "<다음>"))
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
                    .system(sp -> sp.param("text", "<다음> 아래에 데이터를 보고 오늘의 온도를 보고 할만한 활동을 추천해주고 오늘의 대기질과 날씨 데이터와 관련지어서 이유도 설명해줘야돼. activityName과 reason을 나눠서 여러개를 한글로 추천해주고 json 형식으로 출력해줘 \n" +
                            "<다음>\n"))
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
                    .system(sp -> sp.param("text", "<다음> 아래에 데이터를 보고 오늘의 옷을 추천해주는데 clothesName과 reason을 나눠서 여러개를 한글로 추천해주고 json 형식으로 출력해줘 \n" +
                            "<다음>"))
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
