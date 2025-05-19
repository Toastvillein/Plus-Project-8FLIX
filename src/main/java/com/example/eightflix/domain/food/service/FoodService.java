package com.example.eightflix.domain.food.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eightflix.domain.food.dto.request.FoodSaveRequest;
import com.example.eightflix.domain.food.dto.request.FoodUpdateRequest;
import com.example.eightflix.domain.food.dto.response.FoodResponse;
import com.example.eightflix.domain.food.entity.Food;
import com.example.eightflix.domain.food.exception.FoodErrorCode;
import com.example.eightflix.domain.food.repository.FoodRepository;
import com.example.eightflix.global.exception.BizException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodService {

	private final FoodRepository foodRepository;

	@Transactional
	public FoodResponse saveFood(FoodSaveRequest request) {
		if (foodRepository.findByName(request.name()).isPresent()){
			throw new BizException(FoodErrorCode.INVALID_NAME);
		}

		Food food = new Food(request.name(),request.quantity(),request.foodstatus());

		Food save = foodRepository.save(food);

		return FoodResponse.from(save);
	}

	@Transactional
	public FoodResponse updateFood(FoodUpdateRequest request) {

		Food food = foodRepository.findById(request.id()).orElseThrow(
			() -> new BizException(FoodErrorCode.INVALID_ID)
		);

		if(request.name() != null){
			food.updateName(request.name());
		}

		if(request.quantity() != null){
			food.updateQuantity(request.quantity());
		}

		if(request.foodstatus() != null){
			food.updateFoodStatus(request.foodstatus());
		}

		return FoodResponse.from(food);
	}


	@Transactional(readOnly = true)
	public List<FoodSaveResponse> findAllFoods() {
		return  foodRepository.findAll().stream()
			.map(food -> new FoodResponse(
				food.getId(),food.getName(),food.getQuantity(),food.getFoodStatus()))
			.toList();
	}

	@Transactional
	public void deleteFood(Long foodId) {
		Food food = foodRepository.findById(foodId).orElseThrow(
			() -> new BizException(FoodErrorCode.INVALID_ID)
		);

		food.softDelete();
	}
}
