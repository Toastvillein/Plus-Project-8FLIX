package com.example.eightflix.domain.food.service;

import org.springframework.stereotype.Component;

import com.example.eightflix.domain.food.entity.Food;
import com.example.eightflix.domain.food.exception.FoodErrorCode;
import com.example.eightflix.global.exception.BizException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FoodServiceUtil {
	public void decreaseFood(Food food, int quantity){
		if(food.getQuantity()<quantity){
			throw new BizException(FoodErrorCode.INVALID_FOODSTATUS);
		}

		int result = food.getQuantity()-quantity;

		food.updateQuantity(result);
	}
}
