package com.example.sumda.service;

import com.example.sumda.DTO.AirInfoDTO;
import com.example.sumda.DTO.StationDTO;
import com.example.sumda.entity.AirInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

@Service
public class AirInfoService {
    @Value("${api.service.key}")
    private String serviceKey;

    // URL 구성 요소
    private final String BASE_URL = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc";
    private final String apiUri = "/getMsrstnAcctoRltmMesureDnsty"; // 측정소별 실시간 측정정보 조회
    private final String dataTerm = "&dataTerm=DAILY"; // 필수 - 요청 데이터기간(1일: DAILY, 1개월: MONTH, 3개월: 3MONTH)
    private final String defaultQueryParam = "&returnType=json"; // JSON 형식으로 반환
    private final String ver = "&ver=1.4";

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

    // 측정소별 실시간 측정정보 - 대기질 정보
    public AirInfoDTO getLatestAirQualityData(String stationName) throws Exception {
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
                throw new RuntimeException("No air quality data available.");
            }
        } else {
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
