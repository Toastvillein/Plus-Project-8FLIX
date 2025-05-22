package com.example.eightflix.domain.reservation.service;

import static com.example.eightflix.domain.movie.exception.MovieErrorCode.*;
import static com.example.eightflix.domain.reservation.exception.ReservationErrorCode.*;
import static com.example.eightflix.domain.user.exception.UserErrorCode.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.example.eightflix.domain.movie.entity.Movie;
import com.example.eightflix.domain.movie.repository.MovieRepository;
import com.example.eightflix.domain.reservation.dto.request.ReservationRequest;
import com.example.eightflix.domain.reservation.entity.Seat;
import com.example.eightflix.domain.reservation.repository.SeatRepository;
import com.example.eightflix.domain.user.entity.User;
import com.example.eightflix.domain.user.repository.UserRepository;
import com.example.eightflix.global.exception.BizException;

import lombok.RequiredArgsConstructor;

/**
 * Redisson 사용한 락 구현
 */
@Service
@RequiredArgsConstructor
public class RedissonLockService {
	private final MovieRepository movieRepository;
	private final SeatRepository seatRepository;
	private final UserRepository userRepository;
	private final ReservationService reservationService;
	private final RedissonClient redissonClient;

	private static final int MIN_SEAT_COUNT = 1;
	private static final int MAX_SEAT_COUNT = 5;

	public void reserveMovie(Long userId, ReservationRequest reservationRequest) {
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

	private void reserveMovieWithLock(ReservationRequest reservationRequest, User user, Movie movie) {
		// lockKeys 생성
		List<String> lockKeys = reservationRequest.reservationSeats().stream()
			.map(seat -> "lock:seat:" + reservationRequest.movieId() + ":" + seat)
			.toList();

		// 각 key에 대해 RLock 객체 생성
		List<RLock> locks = lockKeys.stream()
			.map(redissonClient::getLock)
			.toList();

		// RedissonMultiLock 생성
		RLock multiLock = new RedissonMultiLock(locks.toArray(new RLock[0]));

		try {
			// 멀티락 획득
			boolean locked = multiLock.tryLock(10, 30, TimeUnit.SECONDS);
			if (!locked) {
				throw new BizException(CANNOT_GET_LOCK);
			}

			reservationService.reserveMovie(user, movie, reservationRequest);
		} catch (InterruptedException e) {
			throw new BizException(INTERRUPTED_LOCK);
		} finally {
			for (RLock lock : locks) {
				if (lock.isLocked() && lock.isHeldByCurrentThread()) {
					lock.unlock();  // 락이 현재 스레드가 획득한 상태일 때만 락 해제
				}
			}
		}
	}

	private static void validateSeats(List<Seat> validSeats, List<String> reservationSeats) {
		List<String> validSeatCodes = validSeats.stream()
			.map(Seat::getSeatCode)
			.toList();

		for (String reservationSeat : reservationSeats) {
			if (!validSeatCodes.contains(reservationSeat)) {
				throw new BizException(UNAVAILABLE_SEAT_ERROR);
			}
		}
	}

	private static void validateSeatCount(int seatSize) {
		if (seatSize > MAX_SEAT_COUNT || seatSize < MIN_SEAT_COUNT) {
			throw new BizException(UNAVAILABLE_SEAT_COUNT_ERROR);
		}
	}
}
