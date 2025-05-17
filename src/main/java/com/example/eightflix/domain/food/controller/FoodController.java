package com.example.eightflix.domain.food.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eightflix.domain.food.dto.request.FoodSaveRequest;
import com.example.eightflix.domain.food.dto.request.FoodUpdateRequest;
import com.example.eightflix.domain.food.dto.response.FoodSaveResponse;
import com.example.eightflix.domain.food.service.FoodService;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
public class FoodController {

	private final FoodService foodService;

	@PostMapping
	public ResponseEntity<FoodSaveResponse> saveFood(@Valid @RequestBody FoodSaveRequest request){

		FoodSaveResponse foods = foodService.saveFood(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(foods);
	}

	@PatchMapping
	public ResponseEntity<FoodSaveResponse> updateFood(@Valid @RequestBody FoodUpdateRequest request){

		FoodSaveResponse foodSaveResponse = foodService.updateFood(request);

		return ResponseEntity.status(HttpStatus.OK).body(foodSaveResponse);
	}

}
