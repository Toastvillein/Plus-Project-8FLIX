package com.example.eightflix.domain.food.service;

import com.example.eightflix.domain.food.dto.request.FoodSaveRequest;
import com.example.eightflix.domain.food.dto.response.FoodSaveResponse;

public interface FoodService {
	FoodSaveResponse saveFood(FoodSaveRequest request);
}
