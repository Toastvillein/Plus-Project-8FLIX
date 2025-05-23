package com.example.eightflix.domain.reservation.service;

import static com.example.eightflix.domain.reservation.exception.ReservationErrorCode.*;

import java.util.List;

import com.example.eightflix.domain.reservation.dto.request.ReservationRequest;
import com.example.eightflix.domain.reservation.entity.Seat;
import com.example.eightflix.global.exception.BizException;

public interface ReservationLockStrategy {
	int MIN_SEAT_COUNT = 1;
	int MAX_SEAT_COUNT = 5;

	void reserveMovie(Long userId, ReservationRequest reservationRequest);

	default void validateSeats(List<Seat> validSeats, List<String> reservationSeats) {
		List<String> validSeatCodes = validSeats.stream()
			.map(Seat::getSeatCode)
			.toList();

		for (String reservationSeat : reservationSeats) {
			if (!validSeatCodes.contains(reservationSeat)) {
				throw new BizException(UNAVAILABLE_SEAT_ERROR);
			}
		}
	}

	default void validateSeatCount(int seatSize) {
		if (seatSize > MAX_SEAT_COUNT || seatSize < MIN_SEAT_COUNT) {
			throw new BizException(UNAVAILABLE_SEAT_COUNT_ERROR);
		}
	}
}