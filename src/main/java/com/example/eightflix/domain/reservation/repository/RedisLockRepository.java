package com.example.eightflix.domain.reservation.repository;

import java.time.Duration;
import java.util.List;

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
			.setIfAbsent(key, "lock", Duration.ofMillis(3000));
	}

	public Long unlock(List<String> key){
		return redisTemplate.delete(key);
	}
}
