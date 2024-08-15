package com.example.sumda.service;

import com.example.sumda.DTO.TMDTO;
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
import java.util.Arrays;
import java.util.List;

@Service
public class TMStdrCrdntService {

    @Value("${api.service.key}")
    private String serviceKey;

    // URL 구성 요소
    private final String BASE_URL = "http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc";
    private final String apiUri = "/getTMStdrCrdnt";
    private final String defaultQueryParam = "&returnType=json"; // JSON 형식으로 반환

    // URL을 생성하는 메서드
    private String makeUrl(String umdName) throws UnsupportedEncodingException {

        // umdName을 URL 인코딩
        String encodedUmdName = URLEncoder.encode(umdName, "UTF-8");

        return new StringBuilder()
                .append(BASE_URL)
                .append(apiUri)
                .append("?ServiceKey=")
                .append(serviceKey)
                .append("&umdName=")
                .append(encodedUmdName) // 예) 혜화동
                .append(defaultQueryParam)
                .toString();
    }

    // OO동으로 tm 주소 얻기
    public List<TMDTO> getTMStdrCrdnt(String umdName) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // 생성된 URL 가져오기
        String url = makeUrl(umdName);
        System.out.println(url);
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
//            TMDTO[] tmArray = objectMapper.treeToValue(itemsNode, TMDTO[].class);

            // itemNode를 리스트로 변환
            List<TMDTO> tmList = objectMapper.convertValue(itemsNode, new TypeReference<List<TMDTO>>() {});
//            TMDTO tmDto = (TMDTO) objectMapper.readValue(itemsNode.traverse(), Object.class);

            return tmList;
        } else {
            throw new RuntimeException("Failed to get data from API: " + response.statusCode());
        }
    }
}
