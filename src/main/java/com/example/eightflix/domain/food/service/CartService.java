package com.example.eightflix.domain.food.service;

import org.springframework.stereotype.Service;

import com.example.eightflix.domain.food.entity.Cart;
import com.example.eightflix.domain.food.repository.CartRepository;
import com.example.eightflix.global.exception.BizException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;

}