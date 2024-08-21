package com.example.sumda.service;

import com.example.sumda.entity.Locations;
import com.example.sumda.repository.LocationRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import io.github.cdimascio.dotenv.Dotenv;

@Service
public class WeatherService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String apiKey;
    private static final String API_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";

    // 생성자에서 Dotenv를 로드하고 API 키를 초기화합니다.
    public WeatherService() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("WEATHER_PUBLIC_API_KEY");
        System.out.println(apiKey);
    }

    public String getLocationById(Long id) {
        Locations location = locationRepository.findById(id).orElse(null);

        if (location != null) {
            // 현재 날짜와 시간 가져오기
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

            // 현재 시간을 가장 가까운 정시 단위로 반올림
            LocalTime roundedTime = roundToNearestHour(now);
            String roundedTimeStr = roundedTime.format(timeFormatter);

            // API 호출
            String apiUrl = String.format("%s?serviceKey=%s&pageNo=%d&numOfRows=%d&dataType=%s&base_date=%s&base_time=%s&nx=%d&ny=%d",
                    API_URL,
                    apiKey, // 주입받은 API 키를 사용합니다.
                    1,
                    1000,
                    "JSON",
                    today.format(dateFormatter),
                    roundedTimeStr,
                    location.getNx(),
                    location.getNy());

            // API 호출 및 응답 파싱
            String apiResponse = restTemplate.getForObject(apiUrl, String.class);

            // 응답 출력
            System.out.println("API Response: " + apiResponse);

            // JSON 응답 파싱
            JSONObject jsonResponse = new JSONObject(apiResponse);
            JSONObject response = jsonResponse.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONObject itemsObj = body.getJSONObject("items");
            JSONArray items = itemsObj.getJSONArray("item");

            // 새로운 JSON 객체 생성
            JSONArray categoryValues = new JSONArray();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                JSONObject categoryValue = new JSONObject();

                String category = item.getString("category");
                String obsrValue = item.getString("obsrValue");

                // 카테고리와 값을 변환
                String meaning = getCategoryMeaning(category, obsrValue);
                categoryValue.put("category", category);
                categoryValue.put("obsrValue", meaning);

                categoryValues.put(categoryValue);
            }

            // 최종 JSON 응답 객체 생성
            JSONObject result = new JSONObject();
            result.put("currentWeather", categoryValues);

            return result.toString();
        }

        return "Location not found";
    }

    private String getCategoryMeaning(String category, String obsrValue) {
        switch (category) {
            case "PTY":
                return getPrecipitationTypeMeaning(obsrValue);
            case "REH":
                return obsrValue + "% (습도)";
            case "RN1":
                return getPrecipitationAmountMeaning(obsrValue);
            case "T1H":
                return obsrValue + "℃ (기온)";
            case "UUU":
                return (Float.parseFloat(obsrValue) > 0 ? "+" : "") + obsrValue + " m/s (동서풍)";
            case "VVV":
                return (Float.parseFloat(obsrValue) > 0 ? "+" : "") + obsrValue + " m/s (남북풍)";
            case "VEC":
                return obsrValue + " deg (풍향)";
            case "WSD":
                return obsrValue + " m/s (풍속)";
            default:
                return obsrValue;
        }
    }

    private String getPrecipitationTypeMeaning(String obsrValue) {
        switch (obsrValue) {
            case "0":
                return "맑음";
            case "1":
                return "비";
            case "2":
                return "비/눈";
            case "3":
                return "눈";
            case "5":
                return "빗방울";
            case "6":
                return "빗방울눈날림";
            case "7":
                return "눈날림";
            default:
                return "알 수 없음";
        }
    }

    private String getPrecipitationAmountMeaning(String obsrValue) {
        float value;
        try {
            value = Float.parseFloat(obsrValue);
        } catch (NumberFormatException e) {
            return "강수없음";
        }

        if (value < 1.0) return "1.0mm 미만";
        else if (value >= 1.0 && value < 30.0) return value + "mm";
        else if (value >= 30.0 && value < 50.0) return "30.0~50.0mm";
        else return "50.0mm 이상";
    }

    private LocalTime roundToNearestHour(LocalTime time) {
        int minutes = time.getMinute();
        int roundedMinutes = (minutes / 60) * 60; // 정시 단위로 반올림
        return time.withMinute(roundedMinutes).withSecond(0).withNano(0);
    }
}
