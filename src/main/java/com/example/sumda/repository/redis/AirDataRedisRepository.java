package com.example.sumda.repository.redis;

import com.example.sumda.entity.redis.RedisAirData;
import org.springframework.data.repository.CrudRepository;

public interface AirDataRedisRepository extends CrudRepository<RedisAirData, Long> {
}
