package com.example.eightflix.domain.food.dto.response;

import com.example.eightflix.domain.food.entity.Food;
import com.example.eightflix.domain.food.entity.FoodStatus;

public record FoodSaveResponse(
	Long id,
	String name,
	int quantity,
	FoodStatus foodStatus
) {
	public static FoodSaveResponse from(Food food){
		return new FoodSaveResponse(food.getId(),food.getName(), food.getQuantity(), food.getFoodStatus());
	}
}
