package com.example.sumda.service;

import com.example.sumda.dto.airinfo.response.AirPollutionImageResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AirPollutionImageService {
    @Value("${api.service.key}")
    private String serviceKey;

    // URL 구성 요소
    private final String BASE_URL = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc";
    private final String apiUri = "/getMinuDustFrcstDspth"; // 대기질 예보통보 조회
    private final String defaultQueryParam = "&returnType=json"; // JSON 형식으로 반환

    // URL을 생성하는 메서드
    private String makeUrl(String informCode) throws UnsupportedEncodingException {

        // 현재 날짜를 "yyyy-MM-dd" 형식으로 가져옴
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // informCode를 URL 인코딩
        String encodeinformCode = URLEncoder.encode(informCode, "UTF-8");

        return new StringBuilder()
                .append(BASE_URL)
                .append(apiUri)
                .append("?ServiceKey=")
                .append(serviceKey)
                .append(defaultQueryParam)
                .append("&informCode=") // 통보코드(PM10, PM25, O3)
                .append(encodeinformCode)
                .append("&searchDate=")
                .append(formattedDate) // 조회날짜
                .toString();
    }

    // 공공 API에서 데이터 가져오기
    public AirPollutionImageResponseDto fetchFromPublicApi() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // 생성된 url 가져오기
        String pm10url = makeUrl("PM10");
        String pm25url = makeUrl("PM25");
        String O3url = makeUrl("O3");

        // HTTP 요청 생성
        HttpRequest pm10Request = HttpRequest.newBuilder()
                .uri(URI.create(pm10url))
                .GET()
                .build();

        HttpRequest pm25Request = HttpRequest.newBuilder()
                .uri(URI.create(pm25url))
                .GET()
                .build();

        HttpRequest O3Request = HttpRequest.newBuilder()
                .uri(URI.create(O3url))
                .GET()
                .build();

        // HTTP 요청 보내기
        HttpResponse<String> pm10response = client.send(pm10Request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> pm25response = client.send(pm25Request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> O3response = client.send(O3Request, HttpResponse.BodyHandlers.ofString());

        // DTO 초기화
        AirPollutionImageResponseDto airDto = new AirPollutionImageResponseDto(); // DTO 객체 생성
        List<AirPollutionImageResponseDto.AirPollutionImage> airPollutionImages = new ArrayList<>(); // 이미지 리스트 생성

        //PM10 처리
        if (pm10response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();

            // JSON 전체를 JsonNode로 파싱
            JsonNode rootNode = objectMapper.readTree(pm10response.body());
            // "items" 배열만 추출
            JsonNode itemsNode = rootNode.path("response").path("body").path("items");
            System.out.println(itemsNode.toString()); // itemsNode 내용 확인

            // 인덱스 0번 값 추출
            if (itemsNode.isArray() && itemsNode.size() > 0) {
                JsonNode firstItemNode = itemsNode.get(0);

                // 특정 항목 추출
                String informCode = firstItemNode.path("informCode").asText();
                String imageUrl1 = firstItemNode.path("imageUrl1").asText();
                String imageUrl2 = firstItemNode.path("imageUrl2").asText();
                String imageUrl3 = firstItemNode.path("imageUrl3").asText();

                // DTO의 이미지 리스트에 추가
                AirPollutionImageResponseDto.AirPollutionImage airPollutionImage = new AirPollutionImageResponseDto.AirPollutionImage();
                airPollutionImage.setInformCode(informCode);

                // 이미지를 리스트로 관리
                List<String> images = new ArrayList<>();
                images.add(imageUrl1);
                images.add(imageUrl2);
                images.add(imageUrl3);

                airPollutionImage.setImages(images);
                airPollutionImages.add(airPollutionImage);
            }

        }

        // PM25 처리
        if (pm25response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();

            // JSON 전체를 JsonNode로 파싱
            JsonNode rootNode = objectMapper.readTree(pm25response.body());
            // "items" 배열만 추출
            JsonNode itemsNode = rootNode.path("response").path("body").path("items");

            // 인덱스 0번 값 추출
            if (itemsNode.isArray() && itemsNode.size() > 0) {
                JsonNode firstItemNode = itemsNode.get(0);

                // 특정 항목 추출
                String informCode = firstItemNode.path("informCode").asText();
                String imageUrl4 = firstItemNode.path("imageUrl4").asText();
                String imageUrl5 = firstItemNode.path("imageUrl5").asText();
                String imageUrl6 = firstItemNode.path("imageUrl6").asText();

                // PM25 이미지 리스트에 추가
                AirPollutionImageResponseDto.AirPollutionImage airPollutionImage = new AirPollutionImageResponseDto.AirPollutionImage();
                airPollutionImage.setInformCode(informCode);

                // 이미지를 리스트로 관리
                List<String> images = new ArrayList<>();
                images.add(imageUrl4);
                images.add(imageUrl5);
                images.add(imageUrl6);

                airPollutionImage.setImages(images);
                airPollutionImages.add(airPollutionImage); // PM25 정보를 리스트에 추가
            }
        }

        // O3 처리
        if (O3response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();

            // JSON 전체를 JsonNode로 파싱
            JsonNode rootNode = objectMapper.readTree(O3response.body());
            // "items" 배열만 추출
            JsonNode itemsNode = rootNode.path("response").path("body").path("items");

            // 인덱스 0번 값 추출
            if (itemsNode.isArray() && itemsNode.size() > 0) {
                JsonNode firstItemNode = itemsNode.get(0);

                // 특정 항목 추출
                String informCode = firstItemNode.path("informCode").asText();
                String imageUrl7 = firstItemNode.path("imageUrl1").asText();
                String imageUrl8 = firstItemNode.path("imageUrl2").asText();
                String imageUrl9 = firstItemNode.path("imageUrl3").asText();

                // PM25 이미지 리스트에 추가
                AirPollutionImageResponseDto.AirPollutionImage airPollutionImage = new AirPollutionImageResponseDto.AirPollutionImage();
                airPollutionImage.setInformCode(informCode);

                // 이미지를 리스트로 관리
                List<String> images = new ArrayList<>();
                images.add(imageUrl7);
                images.add(imageUrl8);
                images.add(imageUrl9);

                airPollutionImage.setImages(images);
                airPollutionImages.add(airPollutionImage); // O3 정보를 리스트에 추가
            }
        }

        // DTO에 이미지 리스트 설정
        airDto.setAirPollutionImages(airPollutionImages);


        return airDto; // DTO 객체 반환
    }


}