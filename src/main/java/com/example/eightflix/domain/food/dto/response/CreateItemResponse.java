package com.example.eightflix.domain.food.dto.response;

import com.example.eightflix.domain.food.entity.CartItem;
import com.example.eightflix.domain.food.entity.Food;

public record CreateItemResponse(
	String foodName,
	int quantity
) {
}
