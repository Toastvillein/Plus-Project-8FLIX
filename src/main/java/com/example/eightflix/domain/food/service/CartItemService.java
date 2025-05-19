package com.example.eightflix.domain.food.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.eightflix.domain.food.dto.response.CreateItemResponse;
import com.example.eightflix.domain.food.entity.Cart;
import com.example.eightflix.domain.food.entity.CartItem;
import com.example.eightflix.domain.food.entity.Food;
import com.example.eightflix.domain.food.entity.FoodStatus;
import com.example.eightflix.domain.food.exception.FoodErrorCode;
import com.example.eightflix.domain.food.repository.CartItemRepository;
import com.example.eightflix.domain.food.repository.CartRepository;
import com.example.eightflix.domain.food.repository.FoodRepository;
import com.example.eightflix.global.exception.BizException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {

	private final CartItemRepository cartItemRepository;
	private final FoodRepository foodRepository;
	private final CartRepository cartRepository;

	public CreateItemResponse createItems(Long foodId,Long cartId,int quantity) {
		Food food = foodRepository.findById(foodId).orElseThrow(
			() -> new BizException(FoodErrorCode.INVALID_ID));

		Cart cart = cartRepository.findById(cartId).orElseThrow(
			() -> new BizException(FoodErrorCode.INVALID_ID));

		if(food.getFoodStatus().equals(FoodStatus.SOLD_OUT)){
			throw new BizException(FoodErrorCode.INVALID_FOODSTATUS);
		}

		cartItemRepository.findByCartAndFood(cart, food).ifPresent(
			item -> {
				throw new BizException(FoodErrorCode.DUPLICATED_LIST);
			});


		CartItem cartItem = new CartItem(quantity,cart,food);

		CartItem save = cartItemRepository.save(cartItem);

		return new CreateItemResponse(food.getName(),quantity);
	}
}
