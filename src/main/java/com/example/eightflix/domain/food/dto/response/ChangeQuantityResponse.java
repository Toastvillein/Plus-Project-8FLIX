package com.example.eightflix.domain.food.dto.response;

import com.example.eightflix.domain.food.entity.CartItem;

public record ChangeQuantityResponse(
	int quantity
) {
	public static ChangeQuantityResponse from(CartItem cartItem){
		return new ChangeQuantityResponse(cartItem.getQuantity());
	}
}
