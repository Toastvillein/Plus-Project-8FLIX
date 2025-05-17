package com.example.eightflix.domain.food.dto.request;

import com.example.eightflix.domain.food.entity.FoodStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FoodSaveRequest(
	@Min(3)
	@NotBlank
	String name,
	@Min(1)
	@NotBlank
	int quantity,
	@NotNull
	FoodStatus foodstatus
) {
}
