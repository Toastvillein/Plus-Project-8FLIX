package com.example.eightflix.domain.food.integration;


import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

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
import com.example.eightflix.domain.food.service.CartItemService;
import com.example.eightflix.global.exception.BizException;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Sql(scripts = "/food_test_db.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CartItemTest {

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("testdb")
		.withUsername("testuser")
		.withPassword("testpass");

	@Autowired
	private FoodRepository foodRepository;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	CartRepository cartRepository;

	@BeforeAll
	public static void beforeAll() {
	}

	@Test
	@Transactional
	void 장바구니_목록_생성(){
		Long foodId = 4L;
		Long cartId = 1L;
		int quantity = 1;

		ItemResponse response = cartItemService.createItems(foodId, cartId, quantity);

		assertThat(response).isNotNull();
		assertThat(response.foodName()).isEqualTo("사이다");
		assertThat(response.quantity()).isEqualTo(1);
	}

	@Test
	@Transactional
	void 장바구니_목록_생성_예외_SOLD_OUT(){
		Long foodId = 5L;
		Long cartId = 1L;
		int quantity = 1;


		assertThatThrownBy(() -> {
			cartItemService.createItems(foodId, cartId, quantity);
		}).isInstanceOf(BizException.class).hasMessageContaining("해당 상품의 재고가 없습니다.");
	}

	@Test
	@Transactional
	void 장바구니_목록_생성_중복_예외(){
		Long foodId = 1L;
		Long cartId = 1L;
		int quantity = 1;

		assertThatThrownBy(() ->{
			cartItemService.createItems(foodId,cartId,quantity);
		}).isInstanceOf(BizException.class).hasMessageContaining("해당 상품이 장바구니에 이미 존재합니다.");
	}

	@Test
	@Transactional
	void 장바구니_목록_호출(){
		Long cartId = 1L;
		List<ItemResponse> responses = cartItemService.findAllItems(cartId);

		assertThat(responses).hasSize(3);
		assertThat(responses).extracting(ItemResponse::foodName)
			.containsExactlyInAnyOrder("콜라","나쵸","팝콘");
		assertThat(responses).extracting(ItemResponse::quantity)
			.containsExactly(10,1000,10);
	}

	@Test
	@Transactional
	void 장바구니_목록_삭제(){
		Long cartItemId = 1L;

		cartItemService.deleteItems(cartItemId);

		Optional<CartItem> deletedItem = cartItemRepository.findById(cartItemId);

		assertThat(deletedItem).isEmpty();
	}

	@Test
	@Transactional
	void 장바구니_목록_수량_변경(){
		Long cartItemId = 1L;

		ChangeQuantityRequest request = new ChangeQuantityRequest(-5);

		ChangeQuantityResponse response = cartItemService.changeQuantity(request, cartItemId);

		assertThat(response.quantity()).isEqualTo(5);
	}

	@Test
	@Transactional
	void 장바구니_목록_수량_변경_0_이하일때_삭제(){
		Long cartItemId = 1L;

		ChangeQuantityRequest request = new ChangeQuantityRequest(-10);

		ChangeQuantityResponse response = cartItemService.changeQuantity(request, cartItemId);
		Optional<CartItem> updatedItem = cartItemRepository.findById(1L);

		assertThat(response.quantity()).isEqualTo(0);
		assertThat(updatedItem).isEmpty();
	}

	@Test
	@Transactional
	void 결제시_장바구니_목록_삭제_및_재고차감(){
		Long cartId = 1L;

		cartItemService.foodPayment(cartId);

		cartItemService.findAllItems(cartId);
		Food food = foodRepository.findById(2L).orElseThrow();

		assertThat(cartItemRepository.findAllByCart(cartId)).isEmpty();
		assertThat(food.getQuantity()).isEqualTo(0);
		assertThat(food.getFoodStatus().name()).isEqualTo("SOLD_OUT");
	}

	@Test
	void 결제_동시성_이슈_테스트() throws InterruptedException {
		int threadCount = 11;
		ExecutorService executor = Executors.newFixedThreadPool(11);
		CountDownLatch latch = new CountDownLatch(threadCount);
		CyclicBarrier barrier = new CyclicBarrier(threadCount);

		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failCount = new AtomicInteger();

		for(int i = 0; i < threadCount; i++){
			long cartId = i+2L;
			executor.submit( () -> {
				try {
					barrier.await();
					cartItemService.foodPayment(cartId);
					successCount.incrementAndGet();
				} catch (BizException e) {
					// 예외 코드 확인해서 실패가 락 문제(FoodErrorCode.LOCK_FAILED)일 경우
					failCount.incrementAndGet();
				} catch (Exception e) {
					e.printStackTrace();
				}  finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executor.shutdown();

		Food food = foodRepository.findById(1L).orElseThrow();
		assertThat(food.getQuantity()).isEqualTo(0); // before = 1000
		assertThat(food.getFoodStatus()).isEqualTo(FoodStatus.SOLD_OUT);

		// 성공 카트는 삭제됨, 실패 카트는 남아 있음
		assertThat(cartItemRepository.findAllByCart(2L)).isEmpty();
		assertThat(cartItemRepository.findAllByCart(12L)).isNotNull();

		// 성공 수 10, 실패 수 1
		assertThat(successCount.get()).isEqualTo(10);
		assertThat(failCount.get()).isEqualTo(1);
	}
}
