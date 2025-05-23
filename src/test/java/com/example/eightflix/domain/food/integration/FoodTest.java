package com.example.eightflix.domain.food.integration;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.eightflix.domain.food.dto.request.FoodSaveRequest;
import com.example.eightflix.domain.food.dto.request.FoodUpdateRequest;
import com.example.eightflix.domain.food.dto.response.FoodResponse;
import com.example.eightflix.domain.food.entity.Food;
import com.example.eightflix.domain.food.entity.FoodStatus;
import com.example.eightflix.domain.food.repository.FoodRepository;
import com.example.eightflix.domain.food.service.FoodService;
import com.example.eightflix.global.exception.BizException;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
@Sql(scripts = "/food_test_db.sql")
public class FoodTest {

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("testdb")
		.withUsername("testuser")
		.withPassword("testpass");

	@Autowired
	private FoodRepository foodRepository;

	@Autowired
	private FoodService foodService;

	@Test
	void 음식정보저장(){
		FoodSaveRequest request = new FoodSaveRequest(
			"쥐포",1000, FoodStatus.FOR_SALE);

		Food food = new Food(request.name(),request.quantity(),request.foodstatus());

		foodRepository.save(food);

		FoodResponse response = FoodResponse.from(food);


		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(6L);
		assertThat(response.name()).isEqualTo("쥐포");
		assertThat(response.quantity()).isEqualTo(1000);
		assertThat(response.foodStatus().name()).isEqualTo("FOR_SALE");
	}

	@Test
	void 음식정보저장_이름이_같아서_예외(){
		FoodSaveRequest request = new FoodSaveRequest(
			"콜라",1000, FoodStatus.FOR_SALE);

		assertThatThrownBy(() -> {
			foodService.saveFood(request);
		}).isInstanceOf(BizException.class)
			.hasMessageContaining("중복된 이름입니다.");
	}

	@Test
	void 음식정보수정(){
		FoodUpdateRequest request = new FoodUpdateRequest(
		1L,"팝콘 달콤한맛",0,FoodStatus.SOLD_OUT);

		FoodResponse response = foodService.updateFood(request);

		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(1);
		assertThat(response.name()).isEqualTo("팝콘 달콤한맛");
		assertThat(response.quantity()).isEqualTo(0);
		assertThat(response.foodStatus().name()).isEqualTo("SOLD_OUT");
	}

	@Test
	void 음식정보수정_존재하지_않는_id_예외(){
		FoodUpdateRequest request = new FoodUpdateRequest(
			7L,"팝콘 달콤한맛",0,FoodStatus.SOLD_OUT);

		assertThatThrownBy( () -> {
				foodService.updateFood(request);
			}).isInstanceOf(BizException.class)
			.hasMessageContaining("유효하지 않은 아이디입니다.");
	}

	@Test
	void 음식정보_전체조회(){
		 List<FoodResponse> response = foodService.findAllFoods();

		 assertThat(response).hasSize(5);
		 assertThat(response)
			 .extracting(FoodResponse::name)
			 .containsExactly("팝콘","나쵸","콜라","사이다","환타");
		 assertThat(response.get(4).foodStatus().name()).isEqualTo("SOLD_OUT");
	}

	@Test
	void 음식정보_삭제(){
		foodService.deleteFood(5L);

		Food food = foodRepository.findById(5L).orElseThrow();

		assertThat(food.getDeletedAt()).isNotNull();
	}
}
