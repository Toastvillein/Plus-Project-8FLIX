package com.example.eightflix.domain.reservation.service;

import static com.example.eightflix.domain.movie.exception.MovieErrorCode.*;
import static com.example.eightflix.domain.reservation.exception.ReservationErrorCode.*;
import static com.example.eightflix.domain.user.exception.UserErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eightflix.domain.movie.entity.Movie;
import com.example.eightflix.domain.movie.repository.MovieRepository;
import com.example.eightflix.domain.reservation.dto.request.ReservationRequest;
import com.example.eightflix.domain.reservation.entity.Seat;
import com.example.eightflix.domain.reservation.repository.SeatRepository;
import com.example.eightflix.domain.user.entity.User;
import com.example.eightflix.domain.user.repository.UserRepository;
import com.example.eightflix.global.exception.BizException;

import lombok.AllArgsConstructor;

/**
 * JPA 비관적 Lock 사용
 */
@Service
@AllArgsConstructor
public class DBLockService implements ReservationLockStrategy {
	private final MovieRepository movieRepository;
	private final SeatRepository seatRepository;
	private final UserRepository userRepository;
	private final ReservationService reservationService;

	@Transactional
	public void reserveMovie(Long userId, ReservationRequest reservationRequest) {
		// movieId 검증
		Movie movie = movieRepository.findById(reservationRequest.movieId())
			.orElseThrow(() -> new BizException(MOVIE_NOT_FOUND));
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BizException(NOT_FOUND_USER));

		// 좌석 검증
		List<Seat> validSeats =
			seatRepository.findValidSeatCodesWithLock(reservationRequest.reservationSeats(), reservationRequest.movieId());
		validateSeatCount(reservationRequest.reservationSeats().size());  // 좌석 개수 제한
		validateSeats(validSeats, reservationRequest.reservationSeats());  // 유효한 좌석인지 검증

		reservationService.reserveMovie(user, movie, reservationRequest);
	}
}
