package com.example.eightflix.domain.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.eightflix.domain.reservation.dto.request.ReservationRequest;
import com.example.eightflix.domain.reservation.repository.RedisLockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LockService {
	private final RedisLockRepository redisLockRepository;
	private final ReservatonService reservatonService;

	public void reserveMovieWithLock(Long userId, ReservationRequest reservationRequest) throws InterruptedException {
		String lockKey = generateLockKey(reservationRequest.movieId(), reservationRequest.reservationSeats());

		// Lock 획득 시도
		while (!redisLockRepository.lock(lockKey)) {
			// SpinLock 방식이 redis 에게 주는 부하를 줄여주기 위한 sleep
			Thread.sleep(100);
		}

		try {
			reservatonService.reserveMovie(userId, reservationRequest);
		} finally {
			redisLockRepository.unlock(lockKey);
		}
	}

	private String generateLockKey(Long movieId, List<String> reservationSeats) {
		return "movie:" + movieId + "seats:" + String.join(",", reservationSeats);
	}
}
