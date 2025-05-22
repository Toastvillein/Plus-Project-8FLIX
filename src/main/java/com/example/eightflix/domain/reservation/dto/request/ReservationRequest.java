package com.example.eightflix.domain.reservation.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ReservationRequest(
	@NotNull Long movieId,
	@NotNull @Valid List<String> reservationSeats
) {}
