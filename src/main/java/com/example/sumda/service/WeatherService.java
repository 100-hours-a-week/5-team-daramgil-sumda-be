package com.example.sumda.service;

import com.example.sumda.entity.Locations;
import com.example.sumda.repository.LocationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WeatherService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate; // JdbcTemplate 주입

    private final String apiKey;
    private static final String CURRENT_API_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";
    private static final String TIME_API_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    private static final String midTermLandForecastUrl = "https://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst";
    private static final String midTermTemperatureUrl ="https://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa";
    // Weather district codes for different regions
    private static final String SEOUL_INCHEON_GYEONGGI_CODE = "11B00000";
    private static final String GANGWON_YEONGSEO_CODE = "11D10000";
    private static final String GANGWON_YEONGDONG_CODE = "11D20000";
    private static final String DAEJEON_SEJONG_CHUNGCHEONGNAM_CODE = "11C20000";
    private static final String CHUNGCHEONGBUK_CODE = "11C10000";
    private static final String GWANGJU_JEOLLANAM_CODE = "11F20000";
    private static final String JEOLLABUK_CODE = "11F10000";
    private static final String DAEGU_GYEONGBUK_CODE = "11H10000";
    private static final String BUSAN_ULSAN_GYEONGNAM_CODE = "11H20000";
    private static final String JEJU_CODE = "11G00000";
    private static final Map<String, String> weatherDistrictCodeMap = new HashMap<>();
    static {
        // 서울, 인천, 경기도
        weatherDistrictCodeMap.put("서울특별시", SEOUL_INCHEON_GYEONGGI_CODE);
        weatherDistrictCodeMap.put("인천광역시", SEOUL_INCHEON_GYEONGGI_CODE);
        weatherDistrictCodeMap.put("경기도", SEOUL_INCHEON_GYEONGGI_CODE);

        // 강원도 영서 지역
        weatherDistrictCodeMap.put("춘천시", GANGWON_YEONGSEO_CODE);
        weatherDistrictCodeMap.put("원주시", GANGWON_YEONGSEO_CODE);
        weatherDistrictCodeMap.put("홍천군", GANGWON_YEONGSEO_CODE);
        weatherDistrictCodeMap.put("횡성군", GANGWON_YEONGSEO_CODE);
        weatherDistrictCodeMap.put("영월군", GANGWON_YEONGSEO_CODE);
        weatherDistrictCodeMap.put("정선군", GANGWON_YEONGSEO_CODE);
        weatherDistrictCodeMap.put("철원군", GANGWON_YEONGSEO_CODE);
        weatherDistrictCodeMap.put("화천군", GANGWON_YEONGSEO_CODE);
        weatherDistrictCodeMap.put("양구군", GANGWON_YEONGSEO_CODE);
        weatherDistrictCodeMap.put("인제군", GANGWON_YEONGSEO_CODE);
        weatherDistrictCodeMap.put("평창군", GANGWON_YEONGSEO_CODE); // 대관령면 제외

        // 강원도 영동 지역
        weatherDistrictCodeMap.put("강릉시", GANGWON_YEONGDONG_CODE);
        weatherDistrictCodeMap.put("동해시", GANGWON_YEONGDONG_CODE);
        weatherDistrictCodeMap.put("삼척시", GANGWON_YEONGDONG_CODE);
        weatherDistrictCodeMap.put("속초시", GANGWON_YEONGDONG_CODE);
        weatherDistrictCodeMap.put("태백시", GANGWON_YEONGDONG_CODE);
        weatherDistrictCodeMap.put("고성군", GANGWON_YEONGDONG_CODE);
        weatherDistrictCodeMap.put("양양군", GANGWON_YEONGDONG_CODE);
        weatherDistrictCodeMap.put("평창군 대관령면", GANGWON_YEONGDONG_CODE); // 특이 케이스

        // 대전, 세종, 충청남도
        weatherDistrictCodeMap.put("대전광역시", DAEJEON_SEJONG_CHUNGCHEONGNAM_CODE);
        weatherDistrictCodeMap.put("세종특별자치시", DAEJEON_SEJONG_CHUNGCHEONGNAM_CODE);
        weatherDistrictCodeMap.put("충청남도", DAEJEON_SEJONG_CHUNGCHEONGNAM_CODE);

        // 충청북도
        weatherDistrictCodeMap.put("충청북도", CHUNGCHEONGBUK_CODE);

        // 광주, 전라남도
        weatherDistrictCodeMap.put("광주광역시", GWANGJU_JEOLLANAM_CODE);
        weatherDistrictCodeMap.put("전라남도", GWANGJU_JEOLLANAM_CODE);

        // 전라북도
        weatherDistrictCodeMap.put("전북특별자치도", JEOLLABUK_CODE);

        // 대구, 경상북도
        weatherDistrictCodeMap.put("대구광역시", DAEGU_GYEONGBUK_CODE);
        weatherDistrictCodeMap.put("경상북도", DAEGU_GYEONGBUK_CODE);

        // 부산, 울산, 경상남도
        weatherDistrictCodeMap.put("부산광역시", BUSAN_ULSAN_GYEONGNAM_CODE);
        weatherDistrictCodeMap.put("울산광역시", BUSAN_ULSAN_GYEONGNAM_CODE);
        weatherDistrictCodeMap.put("경상남도", BUSAN_ULSAN_GYEONGNAM_CODE);

        // 제주도
        weatherDistrictCodeMap.put("제주특별자치도", JEJU_CODE);
    }

    public WeatherService() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("WEATHER_PUBLIC_API_KEY");
    }
    private String getWeatherCodeById(Long id) {
        String sql = "SELECT code FROM location_code_mapping WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, String.class);
    }
    public String getDaysWeatherByID(Long id) {
        Locations location = getLocationById(id);
        if (location == null) {
            System.out.println("Location not found for ID: " + id);
            return "Location not found";
        }

        // 기존의 weatherDistrictCodeMap에서 코드를 가져오는 부분 (원래의 로직 유지)
        String district = location.getDistrict();
        String key = extractKeyFromDistrict(district);
        String weatherCode = weatherDistrictCodeMap.get(key);
        if (weatherCode == null) {
            System.out.println("Weather district code not found for key: " + key);
            return "Weather district code not found";
        }

        // 새로운 location_code_mapping 테이블에서 code 가져오기
        String DweaherCode = getWeatherCodeById(id);
        System.out.println(DweaherCode);

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        String tmFc = determineForecastTime(today, now);

        String landForecastUrl = String.format("%s?serviceKey=%s&pageNo=1&numOfRows=10&dataType=JSON&regId=%s&tmFc=%s",
                midTermLandForecastUrl, apiKey, weatherCode, tmFc);
        String temperatureUrl = String.format("%s?serviceKey=%s&pageNo=1&numOfRows=10&dataType=JSON&regId=%s&tmFc=%s",
                midTermTemperatureUrl, apiKey, DweaherCode, tmFc);

        System.out.println(landForecastUrl);
        System.out.println(temperatureUrl);

        // API 요청 및 응답 출력
        String landForecastResponse = restTemplate.getForObject(landForecastUrl, String.class);
        String temperatureResponse = restTemplate.getForObject(temperatureUrl, String.class);

        System.out.println("Land Forecast API Response:");
        System.out.println(landForecastResponse);

        System.out.println("Temperature Forecast API Response:");
        System.out.println(temperatureResponse);

        JSONObject landForecastJson = new JSONObject(landForecastResponse).getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item").getJSONObject(0);
        JSONObject temperatureForecastJson = new JSONObject(temperatureResponse).getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item").getJSONObject(0);

        JSONObject finalResult = new JSONObject();
        JSONArray daysArray = new JSONArray();

        for (int i = 3; i <= 10; i++) {
            JSONObject dayWeather = new JSONObject();
            dayWeather.put("day", i);

            if (i <= 7) {
                // AM/PM 데이터가 있는 경우
                String amKey = "wf" + i + "Am";
                String pmKey = "wf" + i + "Pm";

                String weatherAm = landForecastJson.optString(amKey, "N/A");
                String weatherPm = landForecastJson.optString(pmKey, "N/A");

                if ("N/A".equals(weatherAm)) {
                    System.out.println("Missing AM weather data for day " + i + " with key: " + amKey);
                }
                if ("N/A".equals(weatherPm)) {
                    System.out.println("Missing PM weather data for day " + i + " with key: " + pmKey);
                }

                dayWeather.put("weatherAm", weatherAm);
                dayWeather.put("weatherPm", weatherPm);
            } else {
                // 8, 9, 10일은 AM/PM이 아닌 단일 값
                String weatherKey = "wf" + i;
                String weather = landForecastJson.optString(weatherKey, "N/A");

                if ("N/A".equals(weather)) {
                    System.out.println("Missing weather data for day " + i + " with key: " + weatherKey);
                }

                dayWeather.put("weather", weather);
            }

            // 최저/최고 온도 처리
            String minKey = "taMin" + i;
            String maxKey = "taMax" + i;

            int minTemperature = temperatureForecastJson.optInt(minKey, 0);
            int maxTemperature = temperatureForecastJson.optInt(maxKey, 0);

            if (minTemperature == 0) {
                System.out.println("Missing Min Temperature data for day " + i + " with key: " + minKey);
            }
            if (maxTemperature == 0) {
                System.out.println("Missing Max Temperature data for day " + i + " with key: " + maxKey);
            }

            dayWeather.put("minTemperature", minTemperature);
            dayWeather.put("maxTemperature", maxTemperature);

            daysArray.put(dayWeather);
        }

        finalResult.put("forecast", daysArray);

        return finalResult.toString(4);
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

    public String getTimeWeatherByID(Long id) throws JsonProcessingException {
        System.out.println("getTimeWeatherByID called with ID: " + id);

        Locations location = getLocationById(id);
        if (location == null) {
            System.out.println("Location not found for ID: " + id);
            return "Location not found";
        }

        System.out.println("Location found: " + location);

        String apiUrl = buildTimeApiUrl(location);
        System.out.println("API URL: " + apiUrl);

        String apiResponse = restTemplate.getForObject(apiUrl, String.class);
        System.out.println("API Response: " + apiResponse);

        String result = processWeatherData(apiResponse, location);
        System.out.println("Processed Weather Data: " + result);

        return result;
    }


    public String getCurrentWeatherById(Long id) {
        Locations location = getLocationById(id);
        if (location == null) return "Location not found";

        String apiUrl = buildCurrentApiUrl(location);
        String apiResponse = restTemplate.getForObject(apiUrl, String.class);

        return buildCurrentWeatherJson(apiResponse);
    }

    private Locations getLocationById(Long id) {
        return locationRepository.findById(id).orElse(null);
    }

    private String buildTimeApiUrl(Locations location) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        String selectedBaseTime = selectNearestBaseTime(now, getBaseTimes());
        String date = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return String.format("%s?serviceKey=%s&pageNo=%d&numOfRows=%d&dataType=%s&base_date=%s&base_time=%s&nx=%d&ny=%d",
                TIME_API_URL,
                apiKey,
                1,
                1000,
                "JSON",
                date,
                selectedBaseTime,
                location.getNx(),
                location.getNy());
    }

    private String buildCurrentApiUrl(Locations location) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        String baseTime = determineBaseTime(now);
        String date = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return String.format("%s?serviceKey=%s&pageNo=%d&numOfRows=%d&dataType=%s&base_date=%s&base_time=%s&nx=%d&ny=%d",
                CURRENT_API_URL,
                apiKey,
                1,
                1000,
                "JSON",
                date,
                baseTime,
                location.getNx(),
                location.getNy());
    }

    private String processWeatherData(String apiResponse, Locations location) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(apiResponse);
        JsonNode items = root.path("response").path("body").path("items").path("item");

        Map<String, JSONObject> informations = new HashMap<>();

        if (items.isArray()) {
            for (JsonNode item : items) {
                String fcstDate = item.path("fcstDate").asText();
                String fcstTime = item.path("fcstTime").asText();
                String category = item.path("category").asText();
                String fcstValue = item.path("fcstValue").asText();

                String dateTimeKey = fcstDate + fcstTime;
                JSONObject weatherData = informations.getOrDefault(dateTimeKey, new JSONObject());

                switch (category) {
                    case "SKY":
                        weatherData.put("sky", getSkyCode().getOrDefault(Integer.parseInt(fcstValue), "알 수 없음"));
                        break;
                    case "PTY":
                        weatherData.put("precipitation", getPtyCode().getOrDefault(Integer.parseInt(fcstValue), "알 수 없음"));
                        break;
                    case "REH":
                        weatherData.put("humidity", fcstValue + "%");
                        break;
                    case "VEC":
                        weatherData.put("windDirection", degToDir(Double.parseDouble(fcstValue), getDegCode()));
                        break;
                    case "WSD":
                        weatherData.put("windSpeed", fcstValue + "m/s");
                        break;
                    // 필요한 다른 카테고리도 추가할 수 있음
                }

                informations.put(dateTimeKey, weatherData);
            }
        }

        // 키(날짜와 시간)를 기준으로 정렬
        List<String> sortedKeys = new ArrayList<>(informations.keySet());
        Collections.sort(sortedKeys);

        JSONArray jsonResultArray = new JSONArray();

        for (String key : sortedKeys) {
            JSONObject weatherData = informations.get(key);

            String date = key.substring(0, 8);
            String time = key.substring(8, 12);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", date);
            jsonObject.put("time", time);
            jsonObject.put("weather", weatherData);

            jsonResultArray.put(jsonObject);
        }

        JSONObject finalResult = new JSONObject();
        finalResult.put("weatherData", jsonResultArray);

        return finalResult.toString(4); // pretty print JSON with indentation
    }



    private String formatWeatherOutput(Map<String, Map<String, String>> informations, Locations location) {
        System.out.println("formatWeatherOutput called with informations: " + informations);
        System.out.println("Location: " + location);

        StringBuilder result = new StringBuilder();
        Map<Integer, String> degCode = getDegCode();
        Map<Integer, String> skyCode = getSkyCode();
        Map<Integer, String> ptyCode = getPtyCode();
        LocalDate today = LocalDate.now();

        for (Map.Entry<String, Map<String, String>> entry : informations.entrySet()) {
            String time = entry.getKey();
            Map<String, String> values = entry.getValue();

            System.out.println("Processing time: " + time + " with values: " + values);

            result.append(String.format("%s년 %s월 %s일 %s시 %s분 (%d, %d) 지역의 날씨는 ",
                    today.format(DateTimeFormatter.ofPattern("yyyy")),
                    today.format(DateTimeFormatter.ofPattern("MM")),
                    today.format(DateTimeFormatter.ofPattern("dd")),
                    time.substring(0, 2),
                    time.substring(2, 4),
                    location.getNx(),
                    location.getNy()));

            if (values.containsKey("SKY")) {
                result.append(skyCode.getOrDefault(Integer.parseInt(values.get("SKY")), "알 수 없음")).append(" ");
            }

            if (values.containsKey("PTY")) {
                result.append(ptyCode.getOrDefault(Integer.parseInt(values.get("PTY")), "알 수 없음")).append(" ");
                if (!"강수 없음".equals(values.get("RN1"))) {
                    result.append("시간당 ").append(values.get("RN1")).append("mm ");
                }
            }

            if (values.containsKey("T1H")) {
                result.append(String.format("기온 %.1f℃ ", Double.parseDouble(values.get("T1H"))));
            }

            if (values.containsKey("REH")) {
                result.append(String.format("습도 %.1f%% ", Double.parseDouble(values.get("REH"))));
            }

            if (values.containsKey("VEC") && values.containsKey("WSD")) {
                result.append(String.format("풍속 %s 방향 %sm/s", degToDir(Double.parseDouble(values.get("VEC")), degCode), values.get("WSD")));
            }

            result.append("\n");
        }

        return result.toString();
    }

    private String buildCurrentWeatherJson(String apiResponse) {
        JSONObject jsonResponse = new JSONObject(apiResponse);
        JSONObject response = jsonResponse.getJSONObject("response");
        JSONObject body = response.getJSONObject("body");
        JSONObject itemsObj = body.getJSONObject("items");
        JSONArray items = itemsObj.getJSONArray("item");

        JSONArray categoryValues = new JSONArray();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            JSONObject categoryValue = new JSONObject();

            String category = item.getString("category");
            String obsrValue = item.getString("obsrValue");

            String mappedCategory = mapCategory(category);
            String mappedValue = mapObsValue(category, obsrValue);

            categoryValue.put("category", mappedCategory);
            categoryValue.put("obsrValue", mappedValue);

            categoryValues.put(categoryValue);
        }

        JSONObject result = new JSONObject();
        result.put("currentWeather", categoryValues);

        return result.toString();
    }

    private String selectNearestBaseTime(LocalTime now, String[] baseTimes) {
        int nowMinutes = now.getHour() * 60 + now.getMinute();
        String closestTime = baseTimes[0];
        int minDifference = Math.abs((now.getHour() * 100 + now.getMinute()) - Integer.parseInt(closestTime));

        for (String time : baseTimes) {
            int timeMinutes = Integer.parseInt(time.substring(0, 2)) * 60;
            int difference = Math.abs(nowMinutes - timeMinutes);
            if (difference < minDifference) {
                minDifference = difference;
                closestTime = time;
            }
        }
        return closestTime;
    }

    private String determineBaseTime(LocalTime now) {
        int hour = now.getHour();
        int minute = now.getMinute();

        if (minute < 10) {
            hour = (hour == 0) ? 23 : hour - 1;
        }

        return String.format("%02d00", hour);
    }

    private String degToDir(double deg, Map<Integer, String> degCode) {
        String closeDir = "";
        double minAbs = 360;
        for (Map.Entry<Integer, String> entry : degCode.entrySet()) {
            double diff = Math.abs(entry.getKey() - deg);
            if (diff < minAbs) {
                minAbs = diff;
                closeDir = entry.getValue();
            }
        }
        return closeDir;
    }

    private Map<Integer, String> getDegCode() {
        Map<Integer, String> degCode = new HashMap<>();
        degCode.put(0, "N");
        degCode.put(360, "N");
        degCode.put(180, "S");
        degCode.put(270, "W");
        degCode.put(90, "E");
        degCode.put(22, "NNE");
        degCode.put(45, "NE");
        degCode.put(67, "ENE");
        degCode.put(112, "ESE");
        degCode.put(135, "SE");
        degCode.put(157, "SSE");
        degCode.put(202, "SSW");
        degCode.put(225, "SW");
        degCode.put(247, "WSW");
        degCode.put(292, "WNW");
        degCode.put(315, "NW");
        degCode.put(337, "NNW");
        return degCode;
    }

    private Map<Integer, String> getSkyCode() {
        Map<Integer, String> skyCode = new HashMap<>();
        skyCode.put(1, "맑음");
        skyCode.put(3, "구름많음");
        skyCode.put(4, "흐림");
        return skyCode;
    }

    private Map<Integer, String> getPtyCode() {
        Map<Integer, String> ptyCode = new HashMap<>();
        ptyCode.put(0, "강수 없음");
        ptyCode.put(1, "비");
        ptyCode.put(2, "비/눈");
        ptyCode.put(3, "눈");
        ptyCode.put(5, "빗방울");
        ptyCode.put(6, "진눈깨비");
        ptyCode.put(7, "눈날림");
        return ptyCode;
    }

    private String mapCategory(String category) {
        switch (category) {
            case "T1H":
                return "기온";
            case "RN1":
                return "1시간 강수량";
            case "UUU":
                return "동서바람성분";
            case "VVV":
                return "남북바람성분";
            case "REH":
                return "습도";
            case "PTY":
                return "강수형태";
            case "VEC":
                return "풍향";
            case "WSD":
                return "풍속";
            default:
                return category;
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

    private String[] getBaseTimes() {
        return new String[]{"0200", "0500", "0800", "1100", "1400", "1700", "2000", "2300"};
    }
}
