package com.example.sumda.repository.redis;

import com.example.sumda.entity.CityWeatherData;
import com.example.sumda.entity.redis.RedisWeatherData;
import org.springframework.data.repository.CrudRepository;

public interface WeatherDataRedisRepository extends CrudRepository<RedisWeatherData, Long> {
}
