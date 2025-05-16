package com.example.eightflix.domain.food.dto.request;

import com.example.eightflix.domain.food.entity.FoodStatus;

import jakarta.validation.constraints.NotNull;

public record FoodUpdateRequest(
	@NotNull
	Long id,
	String name,
	Integer quantity,
	FoodStatus foodstatus
) {
}
