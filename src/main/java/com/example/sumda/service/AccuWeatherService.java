package com.example.sumda.service;

import com.example.sumda.entity.AirQualityStations;
import com.example.sumda.entity.CityWeatherData;
import com.example.sumda.entity.Locations;
import com.example.sumda.entity.redis.RedisLocations;
import com.example.sumda.entity.redis.RedisWeatherData;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.repository.AirQualityStationRepository;
import com.example.sumda.repository.CityWeatherDataRepository;
import com.example.sumda.repository.LocationRepository;
import com.example.sumda.repository.redis.LocationRedisRepository;
import com.example.sumda.repository.redis.WeatherDataRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AccuWeatherService {

    private final LocationRepository locationRepository;
    private final AirQualityStationRepository airQualityStationRepository;
    private final CityWeatherDataRepository cityWeatherDataRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LocationRedisRepository locationRedisRepository;
    private final WeatherDataRedisRepository weatherDataRedisRepository;

    public CityWeatherData getCityWeatherData(Long locationId) {

        // locationId로 관측소명 가져오기
        String redisKey = "locations:" + locationId;
        Map<Object, Object> locationMap = redisTemplate.opsForHash().entries(redisKey);

        Integer cityWeatherId;

        // 레디스에 데이터가 없을 경우 DB에서 조회
        if (locationMap.isEmpty()) {
            // locationId로 Locations DB에서 데이터를 조회
            Locations location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new CustomException(ErrorCode.LOCATION_ERROR));

            // 조회된 Location에서 city_weather_id를 추출
            Long stationId = location.getStation().getId();
            Optional<AirQualityStations> airQualityStations = airQualityStationRepository.findById(stationId);
            String stationName = airQualityStations.get().getStationName();

            // 레디스 객체 생성
            RedisLocations redisLocations = new RedisLocations();
            redisLocations.setId(location.getId());
            redisLocations.setCityWeatherId(location.getCityWeatherId());
            redisLocations.setDistrict(location.getDistrict());
            redisLocations.setLatitude(location.getLatitude());
            redisLocations.setLongitude(location.getLongitude());
            redisLocations.setStationId(location.getStation().getId());
            redisLocations.setStationName(stationName);

            locationRedisRepository.save(redisLocations); // location:1
            cityWeatherId = Math.toIntExact(location.getCityWeatherId());
        } else {
            Object cityWeatherIdObj = locationMap.get("cityWeatherId");

            if (cityWeatherIdObj ==null) {
                throw new CustomException(ErrorCode.REDIS_DATA_ERROR);
            }
            try {
                cityWeatherId = (int) Long.parseLong(cityWeatherIdObj.toString());
            } catch (NumberFormatException e) {
                throw new CustomException(ErrorCode.DATA_FORMAT_ERROR);
            }

        }

        // weatherData 조회
        String weatherRedisKey = "weatherData:" + cityWeatherId;
        Map<Object, Object> weatherDataMap = redisTemplate.opsForHash().entries(weatherRedisKey);

        CityWeatherData cityWeatherData;
        if (weatherDataMap.isEmpty()) {
            // 레디스에 weatherData가 없으면 RDS 조회
            cityWeatherData = cityWeatherDataRepository.findById(cityWeatherId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid city weather ID: " + cityWeatherId));

            // RDS에서 가져온 데이터를 Redis에 저장
            RedisWeatherData redisWeatherData = new RedisWeatherData();
            redisWeatherData.setId(cityWeatherData.getId());
            redisWeatherData.setCityOrGun(cityWeatherData.getCityOrGun());
            redisWeatherData.setLatitude(cityWeatherData.getLatitude());
            redisWeatherData.setLongitude(cityWeatherData.getLongitude());

            // JSON 문자열로 변환하여 Redis에 저장
            redisWeatherData.setWeatherDataJson(cityWeatherData.getWeatherData());

            // Redis에 저장
            weatherDataRedisRepository.save(redisWeatherData);
        } else {
            String weatherDataJson = (String) weatherDataMap.get("weatherDataJson");

            cityWeatherData = new CityWeatherData();
            cityWeatherData.setId(cityWeatherId);
            cityWeatherData.setCityOrGun(weatherDataMap.get("cityOrGun").toString());
            cityWeatherData.setLatitude(Double.valueOf(weatherDataMap.get("latitude").toString()));
            cityWeatherData.setLongitude(Double.valueOf(weatherDataMap.get("longitude").toString()));
            cityWeatherData.setWeatherData(weatherDataJson);
            cityWeatherData.convertWeatherDataToJson();
        }

        // 조회된 데이터를 반환
        return cityWeatherData;
    }
}
