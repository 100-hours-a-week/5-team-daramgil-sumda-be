package com.example.sumda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "city_weather_data")
public class CityWeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "city_or_gun", length = 100)
    private String cityOrGun;

    private Double latitude;

    private Double longitude;

    @JsonIgnore // 이 필드를 JSON 직렬화에서 제외
    @Column(columnDefinition = "json")
    private String weatherData;

    @Transient
    private JsonNode weatherDataJson;

    // 엔티티가 로드된 후에 weatherData 필드를 JSON 객체로 변환
    @PostLoad
    public void convertWeatherDataToJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.weatherDataJson = mapper.readTree(this.weatherData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
