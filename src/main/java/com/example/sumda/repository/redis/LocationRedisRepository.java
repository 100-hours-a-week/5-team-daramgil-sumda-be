package com.example.sumda.repository.redis;

import com.example.sumda.entity.redis.RedisLocations;
import org.springframework.data.repository.CrudRepository;

public interface LocationRedisRepository extends CrudRepository<RedisLocations, Long> {
}
