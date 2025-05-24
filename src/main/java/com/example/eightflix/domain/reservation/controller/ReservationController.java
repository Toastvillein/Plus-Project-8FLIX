package com.example.eightflix.domain.reservation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.eightflix.domain.reservation.dto.request.ReservationRequest;
import com.example.eightflix.domain.reservation.service.ReservationLockStrategy;
import com.example.eightflix.global.security.CurrentUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReservationController {
	private final ReservationLockStrategy redissonLockService;

	@PostMapping("/reservations")
	public void reserveMovie(
		@CurrentUser Long userId,
		@Valid @RequestBody ReservationRequest reservationRequest
	) {
		redissonLockService.reserveMovie(userId, reservationRequest);
	}
}