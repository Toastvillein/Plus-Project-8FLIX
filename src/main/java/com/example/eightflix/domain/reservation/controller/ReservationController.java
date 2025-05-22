package com.example.eightflix.domain.reservation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.eightflix.domain.reservation.dto.request.ReservationRequest;
import com.example.eightflix.domain.reservation.service.LockService;
import com.example.eightflix.domain.reservation.service.RedissonLockService;
import com.example.eightflix.domain.reservation.service.ReservationLockStrategy;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReservationController {
	private final ReservationLockStrategy DBLockService;

	@PostMapping("/reservations")
	public void reserveMovie(
		// @AuthenticationPrincipal User user,
		@Valid @RequestBody ReservationRequest reservationRequest
	) throws InterruptedException {
		DBLockService.reserveMovie(1L, reservationRequest);
	}
}