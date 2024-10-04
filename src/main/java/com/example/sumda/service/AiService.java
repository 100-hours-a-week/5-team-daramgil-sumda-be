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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final ChatClient chatClient;
    private final RedisService redisService;
    private final LocationService locationService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // 동일한 dto로 들어온 요청에 대헤 캐시 적용
    @Cacheable(value = "summaryAiCache", key = "#dto.toString()")
    public WeatherAndAirReviewResponseDto getSummaryAi(WeatherAndAirRequestDto dto) {

        try {
            // Redis에서 캐시데이터 가져오기
            Object cachedResponse = redisTemplate.opsForValue().get(dto.toString());

            // 캐시된 데이터가 있을 경우 JSON 문자열을 객체로 변환
            if (cachedResponse != null) {
                String cachedJson = (String) cachedResponse;
                WeatherAndAirReviewResponseDto responseDto = objectMapper.readValue(cachedJson, WeatherAndAirReviewResponseDto.class);
                return responseDto;
            }

            // 캐시에 저장된 값이 없을 때 AI 동작
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

            WeatherAndAirReviewResponseDto responseDto = WeatherAndAirReviewResponseDto.of(oneLineReview, oneLineWeather, review);

            // Redis에 캐시 저장
            redisTemplate.opsForValue().set(dto.toString(),responseDto);

            return responseDto;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            log.error("AI 서비스 호출 중 오류가 발생했습니다.", e);
            throw new CustomException(ErrorCode.AI_ERROR);
        }
    }

    @Cacheable(value = "activityRecommendCache", key = "#dto.toString()")
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

    @Cacheable(value = "clotheRecommendCache", key = "#dto")
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
            System.out.println(e.getMessage());
            log.error("AI 서비스 호출 중 오류가 발생했습니다.", e);
            throw new CustomException(ErrorCode.AI_ERROR);
        }
    }
    public String handleChat(String question, Map<String, Object> airQuality, Map<String, Object> weather) {
        try {
            // 시스템 프롬프트 정의 - 생활 관련 질문도 답변하고, 날씨 및 대기질과 연관지어 대답하게 수정
            String systemPrompt = "너는 우리 서비스에서 다람쥐 캐릭터와 대화하기 위한 대화형 챗봇 역할을 맡았어. "
                    + "사용자가 질문하는 내용에 대해서 대답해줘야 해. 이 질문은 날씨와 대기질 관련한 질문이야. "
                    + "생활 관련 질문, 예를 들어 대중교통 이용 시 주의사항과 같은 질문도 답변해줘야 하고, 날씨와 대기질에 연관지어 답변해야 해. "
                    + "미세먼지나 대기질에 노출되었을 때 건강에 미치는 영향에 대한 질문도 포함되어 있어. "
                    + "너가 판단하여 이상한 질문은 대답을 하지 않아야 해. "
                    + "제외 질문: 범죄, 나쁜 이야기, 날씨와 대기질 범위에 벗어난 질문. "
                    + "대답하지 않을 경우: '날씨와 대기질 관련되지 않은 질문에 답할 수 없어요!!'를 다람쥐 캐릭터가 말하듯이 이야기해라. "
                    + "답변 형식은 반드시 문자열 형식으로. 이모티콘은 사용하지 말고 다람쥐 캐릭터로 말하듯이 이야기해라. "
                    + "또한 답변할 때 이해하기 어려운 실제 데이터 값 혹은 단위로 표기하지 말고 좋음 보통 나쁨으로 표현해라. "
                    + "대중교통 이용 시 주의사항 같은 생활 관련 질문은 반드시 날씨나 대기질에 연관지어 답변해줘. "
                    + "대답은 1~3개의 문장으로 간결하게 작성해.";

            // 사용자 질문과 대기질, 날씨 정보를 기반으로 AI 호출
            String userPrompt = String.format("질문: %s, 대기질 정보: %s, 날씨 정보: %s", question, airQuality.toString(), weather.toString());

            // AI 응답 받기
            String response = chatClient.prompt()
                    .system(sp -> sp.param("text", systemPrompt))
                    .user(userPrompt)
                    .call()
                    .content();

            // 불필요한 ```json 및 코드 블록 제거
            response = response.replaceAll("```json", "").replaceAll("```", "").trim();

            return response;
        } catch (Exception e) {
            log.error("Chat 서비스 호출 중 오류가 발생했습니다.", e);
            throw new CustomException(ErrorCode.AI_ERROR);
        }
    }
}
