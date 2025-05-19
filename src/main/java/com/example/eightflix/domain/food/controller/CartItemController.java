package com.example.eightflix.domain.food.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.eightflix.domain.food.dto.response.CreateItemResponse;
import com.example.eightflix.domain.food.service.CartItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class CartItemController {

	private final CartItemService cartItemService;

	@PostMapping("/{cartId}")
	public ResponseEntity<CreateItemResponse> createItems(
		@PathVariable Long cartId,
		@RequestParam Long foodId,
		@RequestParam(defaultValue = "1") int quantity){

		CreateItemResponse items = cartItemService.createItems(foodId, cartId, quantity);

		return ResponseEntity.status(HttpStatus.OK).body(items);
	}
}
