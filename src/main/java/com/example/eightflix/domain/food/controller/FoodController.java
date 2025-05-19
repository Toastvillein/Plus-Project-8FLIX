package com.example.eightflix.domain.food.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eightflix.domain.food.dto.request.FoodSaveRequest;
import com.example.eightflix.domain.food.dto.request.FoodUpdateRequest;
import com.example.eightflix.domain.food.dto.response.FoodResponse;
import com.example.eightflix.domain.food.service.FoodService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
public class FoodController {

	private final FoodService foodService;

	@PostMapping
	public ResponseEntity<FoodResponse> saveFood(@Valid @RequestBody FoodSaveRequest request){

		FoodResponse foods = foodService.saveFood(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(foods);
	}

	@PatchMapping
	public ResponseEntity<FoodResponse> updateFood(@Valid @RequestBody FoodUpdateRequest request){

		FoodResponse foodResponse = foodService.updateFood(request);

		return ResponseEntity.status(HttpStatus.OK).body(foodResponse);
	}

	@GetMapping
	public ResponseEntity<List<FoodResponse>> findAllFoods(){

		List<FoodResponse> allFoods = foodService.findAllFoods();

		return ResponseEntity.status(HttpStatus.OK).body(allFoods);
	}

	@DeleteMapping("{foodId}")
	public ResponseEntity<Void> deleteFood(@PathVariable Long foodId){

		foodService.deleteFood(foodId);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping
	public ResponseEntity<List<FoodSaveResponse>> findAllFoods(){

		List<FoodSaveResponse> allFoods = foodService.findAllFoods();

		return ResponseEntity.status(HttpStatus.OK).body(allFoods);
	}

	@DeleteMapping("{foodId}")
	public ResponseEntity<Void> deleteFood(@PathVariable Long foodId){

		foodService.deleteFood(foodId);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
