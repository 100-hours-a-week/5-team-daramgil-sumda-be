package com.example.sumda.service;

import com.example.sumda.DTO.AirInfoDTO;
import com.example.sumda.constans.AirQualityConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class AirInfoService {
    @Value("${api.service.key}")
    private String serviceKey;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatClient chatClient;

    // URL 구성 요소
    private  String BASE_URL = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc";
    private String apiUri = "/getMsrstnAcctoRltmMesureDnsty"; // 측정소별 실시간 측정정보 조회
    private  String dataTerm = "&dataTerm=DAILY"; // 필수 - 요청 데이터기간(1일: DAILY, 1개월: MONTH, 3개월: 3MONTH)
    private  String defaultQueryParam = "&returnType=json"; // JSON 형식으로 반환
    private  String ver = "&ver=1.4";


    // 측정소별 실시간 측정정보 - 대기질 정보
    public AirInfoDTO getLatestAirQualityData(String stationName) throws Exception {
        long startTime = System.currentTimeMillis();
        // Redis에 저장된 데이터가 있는지 확인
        AirInfoDTO airInfoDTO = (AirInfoDTO) redisTemplate.opsForValue().get(stationName);



        if(airInfoDTO == null) {
            airInfoDTO = fetchFromPublicAPi(stationName);
            redisTemplate.opsForValue().set(stationName, airInfoDTO, Duration.ofDays(1));       //1시간으로 TTL 설정
            System.out.println("캐시에 데이터 없을 때 : ");
        } else {
            System.out.println("캐시에 데이터 있을 때 : ");
        }


        // Record end time
        long endTime = System.currentTimeMillis();

        // Calculate duration
        long duration = endTime - startTime;
        System.out.println("Duration: " + duration + "ms");


        String response = chatClient.prompt()
                .system(sp -> sp.param("text", AirQualityConstants.CUTE_RESPONSE_PROMPT))
                .user(airInfoDTO.toString())
                .call()
                .content();
        System.out.println("response: " + response);

        return airInfoDTO;
    }

    // URL을 생성하는 메서드
    private String makeUrl(String stationName) throws UnsupportedEncodingException {

        // umdName을 URL 인코딩
        String encodeStationName = URLEncoder.encode(stationName, "UTF-8");

        return new StringBuilder()
                .append(BASE_URL)
                .append(apiUri)
                .append("?ServiceKey=")
                .append(serviceKey)
                .append("&stationName=") // 측정소명 필수
                .append(encodeStationName)
                .append(dataTerm)
                .append(defaultQueryParam)
                .append(ver)
                .toString();
    }

    // 공공 API에서 데이터 가져오기
    private AirInfoDTO fetchFromPublicAPi(String stationName) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // 생성된 URL 가져오기
        String url = makeUrl(stationName);

        // HTTP 요청 생성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // 요청 보내기
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 응답 처리
        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();

            // JSON 전체를 JsonNode로 파싱
            JsonNode rootNode = objectMapper.readTree(response.body());
            // "items" 배열만 추출
            JsonNode itemsNode = rootNode.path("response").path("body").path("items");
            System.out.println(itemsNode.toString()); // itemsNode의 내용 확인
            List<AirInfoDTO> airInfoList = objectMapper.convertValue(itemsNode, new TypeReference<List<AirInfoDTO>>() {});
            System.out.println(airInfoList); // airInfoList의 내용 확인

            // 리스트가 비어있지 않으면 첫 번째 항목 반환
            if (!airInfoList.isEmpty()) {
                return airInfoList.get(0); // 가장 최근 데이터
            } else {
                log.error("No air quality data available.");
                throw new RuntimeException("No air quality data available.");
            }
        } else {
            log.error("Failed to get data from API: {}", response.statusCode());
            throw new RuntimeException("Failed to get data from API: " + response.statusCode());
        }
    }

    // 시간별 대기질 정보 전달
    public List<AirInfoDTO> getTimeAirQualityData(String stationName) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // 생성된 URL 가져오기
        String url = makeUrl(stationName);

        // HTTP 요청 생성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // 요청 보내기
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 응답 처리
        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();

            // JSON 전체를 JsonNode로 파싱
            JsonNode rootNode = objectMapper.readTree(response.body());
            // "items" 배열만 추출
            JsonNode itemsNode = rootNode.path("response").path("body").path("items");
            // itemsNode를 List<AirInfoDTO>로 변환
            List<AirInfoDTO> airInfoList = objectMapper.convertValue(itemsNode, new TypeReference<List<AirInfoDTO>>() {});

            return airInfoList; // 리스트 전체를 반환
        } else {
            throw new RuntimeException("Failed to get data from API: " + response.statusCode());
        }
    }
}
