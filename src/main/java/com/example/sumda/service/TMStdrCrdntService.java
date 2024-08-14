package com.example.sumda.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
                .append(encodedUmdName)
                .append(defaultQueryParam)
                .toString();
    }

    // 외부 API 호출 메서드
    public String getTMStdrCrdnt(String umdName) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // 생성된 URL 가져오기
        String url = makeUrl(umdName);

        // HTTP 요청 생성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // 요청 보내기
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 응답 처리
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("Failed to get data from API: " + response.statusCode());
        }
    }
}
