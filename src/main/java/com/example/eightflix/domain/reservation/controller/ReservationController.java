package com.example.eightflix.domain.reservation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.eightflix.domain.reservation.dto.request.ReservationRequest;
import com.example.eightflix.domain.reservation.service.ReservatonService;
import com.example.eightflix.domain.user.entity.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReservationController {
	private final ReservatonService reservationService;

	@PostMapping("/reservation")
	public void getPlayingMovies(
		@AuthenticationPrincipal User user,
		@Valid @RequestBody ReservationRequest reservationRequest
	) {
		reservationService.reserveMovie(user, reservationRequest);
	}
}