package com.example.eightflix.domain.reservation.service;

import static com.example.eightflix.domain.reservation.exception.ReservationErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.eightflix.domain.reservation.dto.request.ReservationRequest;
import com.example.eightflix.domain.reservation.entity.Reservation;
import com.example.eightflix.domain.reservation.entity.Seat;
import com.example.eightflix.domain.reservation.repository.RedisLockRepository;
import com.example.eightflix.domain.reservation.repository.SeatRepository;
import com.example.eightflix.global.exception.BizException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LockService {
	private final RedisLockRepository redisLockRepository;
	private final ReservatonService reservatonService;
	private final TransactionTemplate transactionTemplate;
	private final SeatRepository seatRepository;

	public void reserveMovieWithLock(Long userId, ReservationRequest reservationRequest) throws InterruptedException {
		List<String> lockKeys = reservationRequest.reservationSeats().stream()
			.map(seat -> "lock:seat:" + reservationRequest.movieId() + ":" + seat)
			.sorted()
			.toList();

		int maxAttempts = 30;

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

		try {
			// 락 획득이 완료되면 트랜잭션 시작
			transactionTemplate.executeWithoutResult(tx -> {
				Reservation reservation = reservatonService.reserveMovie(userId, reservationRequest);
				for (String seatCode : reservationRequest.reservationSeats()) {
					Seat seat = seatRepository.findBySeatCodeAndMovieMovieId(seatCode, reservationRequest.movieId())
						.orElseThrow(() -> new BizException(UNAVAILABLE_SEAT_ERROR));

					seat.updateReservation(reservation);
				}
			});
		} finally {
			redisLockRepository.unlock(lockKeys);
		}
	}

}
