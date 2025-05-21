package com.example.eightflix.domain.reservation.repository;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisLockRepository {
	private final RedisTemplate<String, String> redisTemplate;

	public Boolean lock(String key){
		return redisTemplate
			.opsForValue()
			.setIfAbsent(key, "lock", Duration.ofMillis(3_000));
	}

	public Boolean unlock(String key){
		return redisTemplate.delete(key);
	}
}
