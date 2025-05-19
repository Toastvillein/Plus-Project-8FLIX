package com.example.eightflix.domain.food.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.eightflix.domain.food.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
}
