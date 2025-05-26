package com.example.eightflix.domain.food.entity;

import org.springframework.stereotype.Component;

import com.example.eightflix.domain.food.exception.FoodErrorCode;
import com.example.eightflix.domain.food.repository.FoodRepository;
import com.example.eightflix.global.exception.BizException;
import com.example.eightflix.global.redisson.DistributedLock;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartItemUtil {
	private final FoodRepository foodRepository;

	@DistributedLock(key = "'FOOD:' + #food.id")
	public void decreaseQuantity(Food food, int quantity) {
		Food lastFood = foodRepository.findById(food.getId())
			.orElseThrow(() -> new BizException(FoodErrorCode.INVALID_ID));

		lastFood.updateQuantity(quantity);
		foodRepository.saveAndFlush(lastFood);
	}
}
