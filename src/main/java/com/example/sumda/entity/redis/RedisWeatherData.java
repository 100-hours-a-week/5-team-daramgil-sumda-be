package com.example.sumda.entity.redis;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@ToString
@RedisHash(value = "weather_data")
public class RedisWeatherData {

    @Id
    private Integer id;

    private String cityOrGun;

    private Double latitude;

    private Double longitude;

    private String weatherDataJson; // weatherData를 JSON으로 저장
}
