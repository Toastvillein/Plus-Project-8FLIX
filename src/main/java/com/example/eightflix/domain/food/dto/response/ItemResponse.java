package com.example.eightflix.domain.food.dto.response;

import com.example.eightflix.domain.food.entity.CartItem;

public record ItemResponse(
	String foodName,
	int quantity
) {
	public static ItemResponse from(CartItem cartItem){
		return new ItemResponse(cartItem.getFood().getName(),cartItem.getQuantity());
	}
}
