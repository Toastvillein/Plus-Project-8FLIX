package com.example.eightflix.domain.food.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.eightflix.domain.food.entity.Cart;
import com.example.eightflix.domain.food.entity.CartItem;
import com.example.eightflix.domain.food.entity.Food;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
	@Query("SELECT i FROM CartItem i JOIN FETCH i.food JOIN FETCH i.cart "
		+ "WHERE i.cart =:cart AND i.food = :food")
	Optional<CartItem> findByCartAndFood(@Param("cart") Cart cart,@Param("food") Food food);

	@Query("SELECT i FROM CartItem i JOIN FETCH i.food WHERE i.cart = :cart")
	Optional<CartItem> findAllByCart(@Param("cart") Cart Cart);
}
