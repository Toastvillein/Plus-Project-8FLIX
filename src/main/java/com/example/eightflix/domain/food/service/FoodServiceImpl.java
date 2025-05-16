package com.example.eightflix.domain.food.service;

import org.springframework.stereotype.Service;

import com.example.eightflix.domain.food.dto.request.FoodSaveRequest;
import com.example.eightflix.domain.food.dto.response.FoodSaveResponse;
import com.example.eightflix.domain.food.entity.Food;
import com.example.eightflix.domain.food.exception.FoodErrorCode;
import com.example.eightflix.domain.food.repository.FoodRepository;
import com.example.eightflix.global.exception.BizException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

	private final FoodRepository foodRepository;

	@Override
	public FoodSaveResponse saveFood(FoodSaveRequest request) {
		if (foodRepository.findByName(request.name()).isPresent()){
			throw new BizException(FoodErrorCode.INVALID_NAME);
		}

		Food food = new Food(request.name(),request.quantity(),request.foodstatus());

		Food save = foodRepository.save(food);

		return FoodSaveResponse.from(save);
	}
}
