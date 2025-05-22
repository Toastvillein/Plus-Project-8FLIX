package com.example.eightflix.domain.food.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eightflix.domain.food.dto.request.ChangeQuantityRequest;
import com.example.eightflix.domain.food.dto.response.ChangeQuantityResponse;
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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {

	private final CartItemRepository cartItemRepository;
	private final FoodRepository foodRepository;
	private final CartRepository cartRepository;
	private final FoodServiceUtil serviceUtil;
	private final RedissonClient redissonClient;

	private static final String FOOD_LOCK_PREFIX = "LOCK:STOCK:";

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

			String lockKey = FOOD_LOCK_PREFIX + food.getId(); // 락 키를  "LOCK:STOCK:{id}" 형태로 생성
			RLock lock = redissonClient.getLock(lockKey); // 분산 락 객체

			boolean isLocked = false;

			try {
				isLocked = lock.tryLock(3,5, TimeUnit.SECONDS); // 최대 3초 기다리고 획득 후 5초뒤 자동 만료
				if(!isLocked){
					throw new BizException(FoodErrorCode.LOCK_FAILED);
				}

				serviceUtil.decreaseFood(food,quantity);
			} catch (InterruptedException e) {
				/* Thread.interrupt() 메서드는 스레드를 중간에 종료시킬 수 있는 메서드
				*  다만 모든 상황에서 스레드가 종료되는건 아님
				*  그 이유는 쓰레드가 일시 정지 상태일때만 정지 시킴
				*  즉, 원하는 조건 및 시점에서 스레드를 종료시키기 위한 함수임
				* */
				Thread.currentThread().interrupt();
				throw new BizException(FoodErrorCode.LOCK_INTERRUPTED);
			} finally {
				if(isLocked && lock.isHeldByCurrentThread()){
					lock.unlock();
				}
			}
		}

		cartItemRepository.deleteAllByCartId(cartId);
	}

	@Transactional
	public ChangeQuantityResponse changeQuantity(ChangeQuantityRequest request, Long cartItemId) {

		CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
			() -> new BizException(FoodErrorCode.INVALID_ID));

		int updatedQuantity = cartItem.getQuantity()+request.quantity();

		if(cartItem.getQuantity()<=0){
			deleteItems(cartItemId);
			return new ChangeQuantityResponse(0);
		}

		cartItem.updateQuantity(updatedQuantity);

		return ChangeQuantityResponse.from(cartItem);
	}
}
