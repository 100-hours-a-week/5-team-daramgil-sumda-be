package com.example.sumda.service;

import com.example.sumda.DTO.AirInfoDTO;
import com.example.sumda.DTO.StationDTO;
import com.example.sumda.DTO.TMDTO;
import com.example.sumda.entity.AirInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MsrstnListService {

    @Value("${api.service.key}")
    private String serviceKey;

    // URL 구성 요소
    private final String BASE_URL = "http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc";
    private final String apiUri = "/getMsrstnList";
    private final String defaultQueryParam = "&returnType=json"; // JSON 형식으로 반환
    private final String numOfRows = "&numOfRows=700"; // 관측소 661개

    // URL을 생성하는 메서드
    private String makeUrl() throws UnsupportedEncodingException {
        return new StringBuilder()
                .append(BASE_URL)
                .append(apiUri)
                .append("?ServiceKey=")
                .append(serviceKey)
                .append(defaultQueryParam)
                .append(numOfRows)
                .toString();
    }

    // 외부 API 호출 메서드
    public List<StationDTO> getMsrstnList() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // 생성된 URL 가져오기
        String url = makeUrl();

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
            // itemsNode를 Item 배열로 변환
//            StationDTO[] stationArray = objectMapper.treeToValue(itemsNode, StationDTO[].class);

            List<StationDTO> stationList = objectMapper.convertValue(itemsNode, new TypeReference<List<StationDTO>>() {});

            return stationList;
        } else {
            throw new RuntimeException("Failed to get data from API: " + response.statusCode());
        }
    }
}
