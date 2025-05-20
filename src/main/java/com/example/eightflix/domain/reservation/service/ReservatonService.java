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
import com.example.eightflix.domain.reservation.entity.Reservation;
import com.example.eightflix.domain.reservation.entity.Seat;
import com.example.eightflix.domain.reservation.repository.ReservationRepository;
import com.example.eightflix.domain.reservation.repository.SeatRepository;
import com.example.eightflix.domain.user.entity.User;
import com.example.eightflix.domain.user.repository.UserRepository;
import com.example.eightflix.global.exception.BizException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservatonService {
	private final MovieRepository movieRepository;
	private final SeatRepository seatRepository;
	private final UserRepository userRepository;
	private final ReservationRepository reservationRepository;

	private static final int MIN_SEAT_COUNT = 1;
	private static final int MAX_SEAT_COUNT = 5;

	@Transactional
	public void reserveMovie(Long userId, ReservationRequest reservationRequest) {
		// movieId 검증
		Movie movie = movieRepository.findById(reservationRequest.movieId())
			.orElseThrow(() -> new BizException(MOVIE_NOT_FOUND));
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BizException(USER_NOT_FOUND));

		List<Seat> validSeats =
			seatRepository.findValidSeatCodes(reservationRequest.reservationSeats(), reservationRequest.movieId());
		validateSeatCount(reservationRequest.reservationSeats().size());  // 좌석 개수 제한
		validateSeats(validSeats, reservationRequest.reservationSeats());  // 유효한 좌석인지 검증

		// 영화 예매
		Reservation reservation = Reservation.builder()
			.user(user)
			.movie(movie)
			.build();
		reservationRepository.save(reservation);

		for (Seat seat : validSeats) {
			seat.updateReservation(reservation);
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

		for (Seat validSeat : validSeats) {
			if (validSeat.getReservation() != null) {
				throw new BizException(ALREADY_RESERVED_SEAT_ERROR);
			}
		}
	}

	private static void validateSeatCount(int seatSize) {
		if (seatSize > MAX_SEAT_COUNT || seatSize < MIN_SEAT_COUNT) {
			throw new BizException(UNAVAILABLE_SEAT_COUNT_ERROR);
		}
	}
}
