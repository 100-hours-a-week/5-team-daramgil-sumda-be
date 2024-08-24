package com.example.sumda.service;

import com.example.sumda.constans.WeatherDistrictCode;
import com.example.sumda.dto.weather.response.CurrentWeatherResponseDto;
import com.example.sumda.dto.weather.response.DaysWeatherResponseDto;
import com.example.sumda.dto.weather.response.TimeWeatherResponseDto;
import com.example.sumda.entity.Locations;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.repository.LocationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {

    private final LocationRepository locationRepository;
    private final JdbcTemplate jdbcTemplate; // JdbcTemplate 주입

    @Value("${api.service.weather.key}")
    private String apiKey;
    private static final String CURRENT_API_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";
    private static final String TIME_API_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    private static final String midTermLandForecastUrl = "https://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst";
    private static final String midTermTemperatureUrl = "https://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa";

    // 현재 날씨 간단 조회
    public CurrentWeatherResponseDto getCurrentWeather(Long id) {
        Locations location = getLocationById(id);

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

        String date = today.format(dateFormatter);
        String time = now.format(timeFormatter);

        String currentWeatherUrl = currentDayBuildUrl(CURRENT_API_URL, apiKey, date, time, location.getNx(), location.getNy());

        String dataResponse = sendRequest(currentWeatherUrl);
        CurrentWeatherResponseDto dto = parseWeatherResponse(dataResponse);

        return dto;
    }

    // buildUrl 메서드
    private String currentDayBuildUrl(String baseUrl, String apiKey, String date, String time, int nx, int ny) {
        try {
            String encodedApiKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8.toString());
            String encodedDate = URLEncoder.encode(date, StandardCharsets.UTF_8.toString());
            String encodedTime = URLEncoder.encode(time, StandardCharsets.UTF_8.toString());

            // URL을 생성합니다. 필요한 매개변수들을 붙입니다.
            return String.format("%s?ServiceKey=%s&base_date=%s&base_time=%s&nx=%d&ny=%d&dataType=json", baseUrl, encodedApiKey, encodedDate, encodedTime, nx, ny);
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // 오류 발생 시 null 반환
        }
    }

    // TODO: API 요청을 할 때 현재 시간이 15:01 이면 15시 데이터가 불러와지는데 이럴 경우 아직 API 서버에 데이터가 업데이트 되지 않아 NULL 값이 받아와짐
    //단기 예보 조회에서 나온 데이터를 Dto 형태로 파싱
    private CurrentWeatherResponseDto parseWeatherResponse(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            // JSON 전체를 JsonNode로 파싱

            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode itemsNode;
            try {
                itemsNode = rootNode.path("response").path("body").path("items").path("item");
            } catch (Exception e) {
                log.error("Failed to parse JSON: {}", e.getMessage());
                throw new CustomException(ErrorCode.JSON_PARSE_ERROR);
            }

            // 각 값을 저장할 변수
            String weather = null;
            String humidity = null;
            String precipitationLastHour = null;
            String temperature = null;
            String eastWestWind = null;
            String windDirection = null;
            String northSouthWind = null;
            String windSpeed = null;

            // items 배열을 순회하면서 값을 추출
            for (JsonNode item : itemsNode) {
                String category = item.path("category").asText();
                String obsrValue = item.path("obsrValue").asText();

                switch (category) {
                    case "PTY":
                        weather = mapObsValue(category, obsrValue);
                        break;
                    case "REH":
                        humidity = mapObsValue(category, obsrValue);
                        break;
                    case "RN1":
                        precipitationLastHour = mapObsValue(category, obsrValue);
                        break;
                    case "T1H":
                        temperature = mapObsValue(category, obsrValue);
                        break;
                    case "UUU":
                        eastWestWind = mapObsValue(category, obsrValue);
                        break;
                    case "VEC":
                        windDirection = mapObsValue(category, obsrValue);
                        break;
                    case "VVV":
                        northSouthWind = mapObsValue(category, obsrValue);
                        break;
                    case "WSD":
                        windSpeed = mapObsValue(category, obsrValue);
                        break;
                }
            }
            // DTO 객체 생성 및 반환
            return CurrentWeatherResponseDto.of(weather, humidity, precipitationLastHour, temperature, eastWestWind, windDirection, northSouthWind, windSpeed);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON: {}", e.getMessage());
            throw new CustomException(ErrorCode.SEVER_ERROR);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 URI입니다: {}", e.getMessage());
            throw new CustomException(ErrorCode.SEVER_ERROR);
        }
    }

    private String mapObsValue(String category, String obsrValue) {
        switch (category) {
            case "T1H":
                return obsrValue + " ℃";
            case "RN1":
                return obsrValue + " mm";
            case "UUU":
            case "VVV":
            case "WSD":
                return obsrValue + " m/s";
            case "REH":
                return obsrValue + " %";
            case "PTY":
                return mapPrecipitationType(obsrValue);
            case "VEC":
                return mapWindDirection(obsrValue);
            default:
                return obsrValue;
        }
    }


    //------------------------------------------------------------------------------------------

    public List<DaysWeatherResponseDto> getDaysWeatherByID(Long id) {


        //여기 부터 시작


        List<DaysWeatherResponseDto> getOneToTWoDaysWeather = getOneToTWoDaysWeather(id);
        List<DaysWeatherResponseDto> getThreeToTenDaysWeather = getFourToTenDaysWeather(id);
        List<DaysWeatherResponseDto> allDaysWeather = new ArrayList<>();

        allDaysWeather.addAll(getOneToTWoDaysWeather);
        allDaysWeather.addAll(getThreeToTenDaysWeather);
        return allDaysWeather;
    }

    private List<DaysWeatherResponseDto> getOneToTWoDaysWeather(Long id) {

        Locations location = getLocationById(id);

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

        String date = today.format(dateFormatter);

        // 해당 시간대 (2, 5, 8, 11, 14, 17, 20, 23시)
        int[] hours = {2, 5, 8, 11, 14, 17, 20, 23};

        // 현재 시간에 가장 가까운 이전 시간 찾기
        LocalTime closestHour = LocalTime.of(2, 0);  // 기본값은 2시

        for (int hour : hours) {
            LocalTime tempTime = LocalTime.of(hour, 0);
            if (!now.isBefore(tempTime)) {  // 현재 시간이 해당 시간대 이후일 때
                closestHour = tempTime;  // 이전 시간으로 업데이트
            } else {
                break;  // 조건을 만족하지 않으면 종료
            }
        }

        String time = closestHour.format(timeFormatter);  // hhmm 형식으로 변환


        String currentWeatherUrl = timeDayBuildUrl(TIME_API_URL, apiKey, date, time, location.getNx(), location.getNy());

        String dataResponse = sendRequest(currentWeatherUrl);

        JSONArray jsonArray;
        try {
            jsonArray = new JSONObject(dataResponse).getJSONObject("response")
                    .getJSONObject("body")
                    .getJSONObject("items")
                    .getJSONArray("item");
        } catch (JSONException e) {
            log.error("Failed to parse JSON: {}", e.getMessage());
            throw new CustomException(ErrorCode.JSON_PARSE_ERROR);
        } catch (NullPointerException e) {
            log.error("Null value encountered in JSON response: {}", e.getMessage());
            throw new CustomException(ErrorCode.NULL_POINTER_ERROR);
        }


        // 오늘 날짜를 기준으로 내일, 모레, 글피 날짜 계산
        String tomorrow = today.plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String dayAfterTomorrow = today.plusDays(2).format(DateTimeFormatter.ofPattern("yyyyMMdd"));


        // 데이터를 시간대별로 그룹화하기 위한 맵
        Map<String, Map<String, String>> dayDataMap = new HashMap<>();

        // JSON 배열을 순회하면서 데이터를 추출
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String fcstDate = jsonObject.getString("fcstDate");
            String fcstTime = jsonObject.getString("fcstTime");
            String category = jsonObject.getString("category");
            String fcstValue = jsonObject.getString("fcstValue");

            // 날짜별로 데이터를 저장할 맵 초기화
            dayDataMap.putIfAbsent(fcstDate, new HashMap<>());

            // PTY (강수 형태)와 SKY (하늘 상태) 데이터 저장
            switch (category) {
                case "TMX":
                    if (fcstTime.equals("1500")) {
                        dayDataMap.get(fcstDate).put("TMX", fcstValue);
                    }
                    break;
                case "TMN":
                    if (fcstTime.equals("0600")) {
                        dayDataMap.get(fcstDate).put("TMN", fcstValue);
                    }
                    break;
                case "PTY":
                    if (fcstTime.equals("0900")) {
                        dayDataMap.get(fcstDate).put("PTY_AM", mapPrecipitationType(fcstValue));
                    } else if (fcstTime.equals("1800")) {
                        dayDataMap.get(fcstDate).put("PTY_PM", mapPrecipitationType(fcstValue));
                    }
                    break;
                case "SKY":
                    if (fcstTime.equals("0900")) {
                        dayDataMap.get(fcstDate).put("SKY_AM", getSkyCondition(fcstValue));
                    } else if (fcstTime.equals("1800")) {
                        dayDataMap.get(fcstDate).put("SKY_PM", getSkyCondition(fcstValue));
                    }
                    break;
            }
        }

        // 데이터 추출 후 DTO로 변환하여 리스트에 저장
        List<DaysWeatherResponseDto> daysWeatherList = new ArrayList<>();

        for (Map.Entry<String, Map<String, String>> entry : dayDataMap.entrySet()) {
            String currentDate = entry.getKey();


            Map<String, String> weatherData = entry.getValue();

            String maxTemperature = (weatherData.getOrDefault("TMX", "0"));
            String minTemperature = (weatherData.getOrDefault("TMN", "0"));
            String precipitationAm = weatherData.getOrDefault("PTY_AM", "맑음");
            String skyAm = weatherData.getOrDefault("SKY_AM", "N/A");
            String precipitationPm = weatherData.getOrDefault("PTY_PM", "맑음");
            String skyPm = weatherData.getOrDefault("SKY_PM", "N/A");

            // 오전, 오후 날씨 결정
            String weatherAm = skyAm + (precipitationAm.equals("맑음") ? "" : " 및 " + precipitationAm);
            String weatherPm = skyPm + (precipitationPm.equals("맑음") ? "" : " 및 " + precipitationPm);

            int dayOffset = getDayOffsetFromToday(currentDate, tomorrow, dayAfterTomorrow);


            if (dayOffset != -1) {  // 오늘 날짜는 제외
                DaysWeatherResponseDto dto = DaysWeatherResponseDto.of(
                        maxTemperature,
                        minTemperature,
                        dayOffset,
                        weatherAm,
                        weatherPm
                );

                daysWeatherList.add(dto);
            }

        }

        // 결과 반환
        return daysWeatherList;

    }

    // 날짜에 따른 dayOffset 계산 함수
    private int getDayOffsetFromToday(String fcstDate, String tomorrow, String dayAfterTomorrow) {
        if (fcstDate.equals(tomorrow)) {
            return 1;  // 내일
        } else if (fcstDate.equals(dayAfterTomorrow)) {
            return 2;  // 모레
        } else {
            return -1;  // 오늘
        }
    }


    private List<DaysWeatherResponseDto> getFourToTenDaysWeather(Long id) {
        Locations location = getLocationById(id);

        // 기존의 weatherDistrictCodeMap에서 코드를 가져오는 부분 (원래의 로직 유지)
        String district = location.getDistrict();
        String key = extractKeyFromDistrict(district);
        String weatherCode = WeatherDistrictCode.getCodeByDistrict(key);

        // 새로운 location_code_mapping 테이블에서 code 가져오기
        String dWeatherCode = location.getCode();
        log.info("dWeatherCode: {}", dWeatherCode);

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        String tmFc = determineForecastTime(today, now);

        String landForecastUrl = buildUrl(midTermLandForecastUrl, apiKey, weatherCode, tmFc);
        String temperatureUrl = buildUrl(midTermTemperatureUrl, apiKey, dWeatherCode, tmFc);

        // API 요청 및 응답 출력
        String landForecastResponse = sendRequest(landForecastUrl);
        String temperatureResponse = sendRequest(temperatureUrl);


        JSONObject landForecastJson = new JSONObject(landForecastResponse).getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item").getJSONObject(0);
        JSONObject temperatureForecastJson = new JSONObject(temperatureResponse).getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item").getJSONObject(0);

        List<DaysWeatherResponseDto> weatherList = new ArrayList<>();

        for (int i = 3; i <= 10; i++) {
            String weatherAm = null;
            String weatherPm = null;
            String weather = null;
            int minTemperature = 0;
            int maxTemperature = 0;

            if (i <= 7) {
                // AM/PM 데이터가 있는 경우
                String amKey = "wf" + i + "Am";
                String pmKey = "wf" + i + "Pm";

                weatherAm = landForecastJson.optString(amKey, "N/A");
                weatherPm = landForecastJson.optString(pmKey, "N/A");

            } else {
                // 8, 9, 10일은 AM/PM이 아닌 단일 값
                String weatherKey = "wf" + i;
                weather = landForecastJson.optString(weatherKey, "N/A");

            }

            // 최저/최고 온도 처리
            String minKey = "taMin" + i;
            String maxKey = "taMax" + i;

            minTemperature = temperatureForecastJson.optInt(minKey, 0);
            maxTemperature = temperatureForecastJson.optInt(maxKey, 0);

            DaysWeatherResponseDto dto;
            if (i <= 7) {
                dto = DaysWeatherResponseDto.of(
                        String.valueOf(maxTemperature),
                        String.valueOf(minTemperature),
                        i,
                        weatherAm != null ? weatherAm : "N/A",
                        weatherPm != null ? weatherPm : "N/A"
                );
            } else {
                dto = DaysWeatherResponseDto.of(
                        String.valueOf(maxTemperature),
                        String.valueOf(minTemperature),
                        i,
                        weather != null ? weather : "N/A",
                        null
                );
            }
            weatherList.add(dto);
        }

        return weatherList;
    }


    private String sendRequest(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to get data from API: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            log.error("Failed to send request: {}", e.getMessage());
            throw new CustomException(ErrorCode.SEVER_ERROR);
        }
    }


    private String determineForecastTime(LocalDate date, LocalTime time) {
        LocalTime sixAM = LocalTime.of(6, 0);
        LocalTime sixPM = LocalTime.of(18, 0);

        if (time.isBefore(sixAM)) {
            date = date.minusDays(1);
            time = sixPM;
        } else if (time.isBefore(sixPM)) {
            time = sixAM;
        } else {
            time = sixPM;
        }

        return String.format("%s%02d00", date.format(DateTimeFormatter.ofPattern("yyyyMMdd")), time.getHour());
    }


    private String extractKeyFromDistrict(String district) {
        // "강원특별자치도 강릉시 강동면"과 같은 형식에서 시/군/구 추출
        String[] parts = district.split(" ");

        if (parts.length >= 3 && parts[0].equals("강원특별자치도")) {
            // 강원특별자치도의 경우 시/군/구가 세 번째 요소
            if (parts[1].equals("평창군") && parts[2].equals("대관령면")) {
                // 특이 케이스: 평창군 대관령면은 영동 지역으로 처리
                return "평창군 대관령면";
            }
            return parts[1]; // 강원특별자치도의 경우 시/군/구 반환
        } else if (parts.length >= 2) {
            // 강원특별자치도가 아닌 경우 첫 번째 요소 반환
            return parts[0]; // 시/군/구 또는 도/시 반환
        }

        return district; // 기본적으로 전체 주소를 반환
    }

    private Locations getLocationById(Long id) {
        return locationRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.LOCATION_ERROR));
    }


    // buildUrl 메서드
    private String buildUrl(String baseUrl, String apiKey, String code, String tmFc) {
        try {
            String encodedApiKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8.toString());
            String encodedCode = URLEncoder.encode(code, StandardCharsets.UTF_8.toString());
            String encodedTmFc = URLEncoder.encode(tmFc, StandardCharsets.UTF_8.toString());

            // URL을 생성합니다. 필요한 매개변수들을 붙입니다.
            return String.format("%s?ServiceKey=%s&regId=%s&tmFc=%s&dataType=json", baseUrl, encodedApiKey, encodedCode, encodedTmFc);
        } catch (Exception e) {
            log.error("잘못된 URI입니다: {}", e.getMessage());
            throw new CustomException(ErrorCode.SEVER_ERROR);
        }
    }


    private String mapPrecipitationType(String obsrValue) {
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

    private String mapWindDirection(String obsrValue) {
        int windDirection = Integer.parseInt(obsrValue);

        if (windDirection >= 0 && windDirection < 45) {
            return "N-NE";
        } else if (windDirection >= 45 && windDirection < 90) {
            return "NE-E";
        } else if (windDirection >= 90 && windDirection < 135) {
            return "E-SE";
        } else if (windDirection >= 135 && windDirection < 180) {
            return "SE-S";
        } else if (windDirection >= 180 && windDirection < 225) {
            return "S-SW";
        } else if (windDirection >= 225 && windDirection < 270) {
            return "SW-W";
        } else if (windDirection >= 270 && windDirection < 315) {
            return "W-NW";
        } else if (windDirection >= 315 && windDirection <= 360) {
            return "NW-N";
        } else {
            return "알 수 없음";
        }
    }



    /* ----------------------------------------------------------------------------------------------- */

    // 시간별 날씨 조회
    public List<TimeWeatherResponseDto> getTimeWeather(Long id) {

        Locations location = getLocationById(id);

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

        String date = today.format(dateFormatter);
        // 해당 시간대 (2, 5, 8, 11, 14, 17, 20, 23시)
        int[] hours = {2, 5, 8, 11, 14, 17, 20, 23};

        // 현재 시간에 가장 가까운 이전 시간 찾기
        LocalTime closestHour = LocalTime.of(2, 0);  // 기본값은 2시

        for (int hour : hours) {
            LocalTime tempTime = LocalTime.of(hour, 0);
            if (!now.isBefore(tempTime)) {  // 현재 시간이 해당 시간대 이후일 때
                closestHour = tempTime;  // 이전 시간으로 업데이트
            } else {
                break;  // 조건을 만족하지 않으면 종료
            }
        }

        String time = closestHour.format(timeFormatter);  // hhmm 형식으로 변환

        String currentWeatherUrl = timeDayBuildUrl(TIME_API_URL, apiKey, date, time, location.getNx(), location.getNy());

        String dataResponse = sendRequest(currentWeatherUrl);


        JSONArray jsonArray;
        try {
            jsonArray = new JSONObject(dataResponse).getJSONObject("response")
                    .getJSONObject("body")
                    .getJSONObject("items")
                    .getJSONArray("item");

        } catch (JSONException e) {

            log.error("Failed to parse JSON: {}", e.getMessage());
            log.error("URL: {}", currentWeatherUrl);
            throw new CustomException(ErrorCode.JSON_PARSE_ERROR);
        } catch (NullPointerException e) {
            log.error("Null value encountered in JSON response: {}", e.getMessage());
            throw new CustomException(ErrorCode.NULL_POINTER_ERROR);
        }

        List<TimeWeatherResponseDto> weatherList = new ArrayList<>();
        // 데이터를 시간대별로 그룹화하기 위한 맵
        Map<String, Map<String, String>> timeDataMap = new HashMap<>();

        // JSON 배열을 순회하면서 데이터 추출 및 그룹화
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String fcstDate = jsonObject.getString("fcstDate");
            String fcstTime = jsonObject.getString("fcstTime");
            String category = jsonObject.getString("category");
            String value = jsonObject.getString("fcstValue");

            String dateTimeKey = fcstDate + fcstTime;

            // 해당 시간대 데이터가 없으면 새로 추가
            timeDataMap.putIfAbsent(dateTimeKey, new HashMap<>());
            Map<String, String> dataMap = timeDataMap.get(dateTimeKey);
            dataMap.put(category, value);
        }

        // 각 시간대별 데이터를 DTO로 변환
        for (Map.Entry<String, Map<String, String>> entry : timeDataMap.entrySet()) {
            String dateTimeKey = entry.getKey();
            String fcstDate = dateTimeKey.substring(0, 8); // 날짜 부분 추출
            String fcstTime = dateTimeKey.substring(8);   // 시간 부분 추출
            Map<String, String> dataMap = entry.getValue();

            // 데이터를 DTO에 매핑
            String sky = getSkyCondition(dataMap.get("SKY"));
            String precipitation = mapPrecipitationType(dataMap.get("PTY"));
            String humidity = dataMap.getOrDefault("REH", "0") + "%";
            String windDirection = dataMap.getOrDefault("VEC", "0") + "°";
            String windSpeed = dataMap.getOrDefault("WSD", "0") + "m/s";

            TimeWeatherResponseDto.Weather weather = new TimeWeatherResponseDto.Weather(
                    sky,
                    precipitation,
                    humidity,
                    windDirection,
                    windSpeed
            );

            TimeWeatherResponseDto dto = new TimeWeatherResponseDto(fcstDate, fcstTime, weather);

            weatherList.add(dto);
        }

        return weatherList;
    }

    private String getSkyCondition(String skyValue) {
        switch (skyValue) {
            case "1":
                return "맑음";
            case "3":
                return "구름많음";
            case "4":
                return "흐림";
            default:
                return "알 수 없음";
        }
    }

    private String timeDayBuildUrl(String baseUrl, String apiKey, String date, String time, int nx, int ny) {
        try {
            String encodedApiKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8.toString());
            String encodedDate = URLEncoder.encode(date, StandardCharsets.UTF_8.toString());
            String encodedTime = URLEncoder.encode(time, StandardCharsets.UTF_8.toString());

            // URL을 생성합니다. 필요한 매개변수들을 붙입니다.
            return String.format("%s?ServiceKey=%s&base_date=%s&base_time=%s&nx=%d&ny=%d&dataType=json&pageNo=1&numOfRows=700", baseUrl, encodedApiKey, encodedDate, encodedTime, nx, ny);
        } catch (Exception e) {
            log.error("잘못된 URI입니다: {}", e.getMessage());
            throw new CustomException(ErrorCode.SEVER_ERROR);
        }
    }
}