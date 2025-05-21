package com.example.eightflix.domain.food.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eightflix.domain.food.dto.response.ItemResponse;
import com.example.eightflix.domain.food.entity.Cart;
import com.example.eightflix.domain.food.entity.CartItem;
import com.example.eightflix.domain.food.entity.Food;
import com.example.eightflix.domain.food.entity.FoodStatus;
import com.example.eightflix.domain.food.exception.FoodErrorCode;
import com.example.eightflix.domain.food.repository.CartItemRepository;
import com.example.eightflix.domain.food.repository.CartRepository;
import com.example.eightflix.domain.food.repository.FoodRepository;
import com.example.eightflix.global.exception.BizException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {

	private final CartItemRepository cartItemRepository;
	private final FoodRepository foodRepository;
	private final CartRepository cartRepository;
	private final FoodServiceUtil serviceUtil;

	@Transactional
	public ItemResponse createItems(Long foodId,Long cartId,int quantity) {
		Food food = foodRepository.findById(foodId).orElseThrow(
			() -> new BizException(FoodErrorCode.INVALID_ID));

		Cart cart = cartRepository.findById(cartId).orElseThrow(
			() -> new BizException(FoodErrorCode.INVALID_ID));

		if(food.getFoodStatus().equals(FoodStatus.SOLD_OUT)){
			throw new BizException(FoodErrorCode.INVALID_FOODSTATUS);
		}

		cartItemRepository.findByCartAndFood(cart, food).ifPresent(
			item -> {
				throw new BizException(FoodErrorCode.DUPLICATED_LIST);
			});


		CartItem cartItem = new CartItem(quantity,cart,food);

		CartItem save = cartItemRepository.save(cartItem);

		return ItemResponse.from(save);
	}

	@Transactional(readOnly = true)
	public List<ItemResponse> findAllItems(Long cartId) {

		List<CartItem> allByCart = cartItemRepository.findAllByCart(cartId);

		return allByCart.stream().map(ItemResponse::from).toList();
	}

	@Transactional
	public void deleteItems(Long cartItemId) {
		CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
			() -> new BizException(FoodErrorCode.INVALID_ID));

		cartItemRepository.delete(cartItem);
	}

	@Transactional
	public void foodPayment(Long cartId) {
		List<CartItem> cartItems = cartItemRepository.findAllByCart(cartId);

		if(cartItems.isEmpty()){
			throw new BizException(FoodErrorCode.NO_CONTENTS);
		}

		for(CartItem cartItem : cartItems){
			Food food = cartItem.getFood();
			int quantity = cartItem.getQuantity();

			serviceUtil.decreaseFood(food,quantity);
		}

		cartItemRepository.deleteAllByCartId(cartId);
	}
}
