package com.example.eightflix.domain.food.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.eightflix.domain.food.dto.response.ItemResponse;
import com.example.eightflix.domain.food.service.CartItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class CartItemController {

	private final CartItemService cartItemService;

	@PostMapping("/{cartId}")
	public ResponseEntity<ItemResponse> createItems(
		@PathVariable Long cartId,
		@RequestParam Long foodId,
		@RequestParam(defaultValue = "1") int quantity){

		ItemResponse items = cartItemService.createItems(foodId, cartId, quantity);

		return ResponseEntity.status(HttpStatus.OK).body(items);
	}

	@GetMapping("/{cartId}")
	public ResponseEntity<List<ItemResponse>> findAllItems(@PathVariable Long cartId){

		List<ItemResponse> allItems = cartItemService.findAllItems(cartId);

		return ResponseEntity.status(HttpStatus.OK).body(allItems);
	}

	@DeleteMapping("/{cartItemId}")
	public ResponseEntity<Void> deleteItems(@PathVariable Long cartItemId){

		cartItemService.deleteItems(cartItemId);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PatchMapping("/{cartId}")
	public ResponseEntity<Void> foodPayment(@PathVariable Long cartId){

		cartItemService.foodPayment(cartId);

		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
