package com.example.eightflix.domain.food.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.eightflix.domain.food.entity.Food;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Repository
public interface FoodRepository extends JpaRepository<Food,Long> {
	Optional<Food> findByName(String name);
}
