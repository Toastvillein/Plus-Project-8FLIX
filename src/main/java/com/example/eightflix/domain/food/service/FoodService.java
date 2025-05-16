package com.example.eightflix.domain.food.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eightflix.domain.food.dto.request.FoodSaveRequest;
import com.example.eightflix.domain.food.dto.request.FoodUpdateRequest;
import com.example.eightflix.domain.food.dto.response.FoodSaveResponse;
import com.example.eightflix.domain.food.entity.Food;
import com.example.eightflix.domain.food.exception.FoodErrorCode;
import com.example.eightflix.domain.food.repository.FoodRepository;
import com.example.eightflix.global.exception.BizException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodService {

	private final FoodRepository foodRepository;

	@Transactional
	public FoodSaveResponse saveFood(FoodSaveRequest request) {
		if (foodRepository.findByName(request.name()).isPresent()){
			throw new BizException(FoodErrorCode.INVALID_NAME);
		}

		Food food = new Food(request.name(),request.quantity(),request.foodstatus());

		Food save = foodRepository.save(food);

		return FoodSaveResponse.from(save);
	}

	@Transactional
	public FoodSaveResponse updateFood(FoodUpdateRequest request) {

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

		return FoodSaveResponse.from(food);
	}

	public List<FoodSaveResponse> findAllFoods() {
		return  foodRepository.findAll().stream()
			.map(food -> new FoodSaveResponse(
				food.getId(),food.getName(),food.getQuantity(),food.getFoodStatus()))
			.toList();
	}
}
