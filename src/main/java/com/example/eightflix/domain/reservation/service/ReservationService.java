package com.example.eightflix.domain.reservation.service;

import static com.example.eightflix.domain.reservation.exception.ReservationErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eightflix.domain.movie.entity.Movie;
import com.example.eightflix.domain.reservation.dto.request.ReservationRequest;
import com.example.eightflix.domain.reservation.entity.Reservation;
import com.example.eightflix.domain.reservation.entity.Seat;
import com.example.eightflix.domain.reservation.repository.ReservationRepository;
import com.example.eightflix.domain.reservation.repository.SeatRepository;
import com.example.eightflix.domain.user.entity.User;
import com.example.eightflix.global.exception.BizException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final SeatRepository seatRepository;
	private final ReservationRepository reservationRepository;

	@Transactional
	public void reserveMovie(User user, Movie movie, ReservationRequest reservationRequest) {
		// 영화 예매
		Reservation reservation = Reservation.builder()
			.user(user)
			.movie(movie)
			.build();

		Reservation savedReservation = reservationRepository.save(reservation);

		// 좌석 예매
		for (String seatCode : reservationRequest.reservationSeats()) {
			Seat seat = seatRepository.findBySeatCodeAndMovieMovieId(seatCode, reservationRequest.movieId())
				.orElseThrow(() -> new BizException(UNAVAILABLE_SEAT_ERROR));

			if (seat.getReservation() != null) {
				throw new BizException(ALREADY_RESERVED_SEAT_ERROR);
			}

			seat.updateReservation(savedReservation);
		}
	}
}
