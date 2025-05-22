package com.example.eightflix.domain.reservation.service;

import static com.example.eightflix.domain.movie.exception.MovieErrorCode.*;
import static com.example.eightflix.domain.reservation.exception.ReservationErrorCode.*;
import static com.example.eightflix.domain.user.exception.UserErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.eightflix.domain.movie.entity.Movie;
import com.example.eightflix.domain.movie.repository.MovieRepository;
import com.example.eightflix.domain.reservation.dto.request.ReservationRequest;
import com.example.eightflix.domain.reservation.entity.Seat;
import com.example.eightflix.domain.reservation.repository.RedisLockRepository;
import com.example.eightflix.domain.reservation.repository.SeatRepository;
import com.example.eightflix.domain.user.entity.User;
import com.example.eightflix.domain.user.repository.UserRepository;
import com.example.eightflix.global.exception.BizException;

import lombok.RequiredArgsConstructor;

/**
 * Lettuce 사용한 락 구현
 */
@Service
@RequiredArgsConstructor
public class LockService implements ReservationLockStrategy {
	private final RedisLockRepository redisLockRepository;
	private final ReservationService reservatonService;
	private final SeatRepository seatRepository;
	private final MovieRepository movieRepository;
	private final UserRepository userRepository;

	public void reserveMovie(Long userId, ReservationRequest reservationRequest) throws InterruptedException {
		// movieId 검증
		Movie movie = movieRepository.findById(reservationRequest.movieId())
			.orElseThrow(() -> new BizException(MOVIE_NOT_FOUND));
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BizException(NOT_FOUND_USER));

		// 좌석 검증
		List<Seat> validSeats =
			seatRepository.findValidSeatCodes(reservationRequest.reservationSeats(), reservationRequest.movieId());
		validateSeatCount(reservationRequest.reservationSeats().size());  // 좌석 개수 제한
		validateSeats(validSeats, reservationRequest.reservationSeats());  // 유효한 좌석인지 검증

		reserveMovieWithLock(reservationRequest, user, movie);
	}

	private void reserveMovieWithLock(ReservationRequest reservationRequest, User user, Movie movie) throws InterruptedException {
		List<String> lockKeys = reservationRequest.reservationSeats().stream()
			.map(seat -> "lock:seat:" + reservationRequest.movieId() + ":" + seat)
			.sorted()
			.toList();

		int maxAttempts = 30;

		try {
			// 트랜잭션 전에 좌석에 대한 모든 락을 먼저 획득
			for (String key : lockKeys) {
				int attempts = 0;
				while (!redisLockRepository.lock(key)) {
					Thread.sleep(100);
					if (++attempts > maxAttempts) {
						redisLockRepository.unlock(lockKeys); // 기존 락 해제
						throw new BizException(ALREADY_RESERVED_SEAT_ERROR);
					}
				}
			}

			// 락 획득이 완료되면 트랜잭션 시작
			reservatonService.reserveMovie(user, movie, reservationRequest);
		} finally {
			redisLockRepository.unlock(lockKeys);
		}
	}
}
