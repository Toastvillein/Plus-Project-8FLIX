package com.example.eightflix.domain.food.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.eightflix.domain.food.entity.Cart;
import com.example.eightflix.domain.food.entity.CartItem;
import com.example.eightflix.domain.food.entity.Food;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
	Optional<CartItem> findByCartAndFood(Cart cart, Food food);
}
