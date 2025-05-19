package com.example.eightflix.domain.food.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.eightflix.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "food")
public class Food extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private int quantity;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FoodStatus foodStatus;

	@OneToMany(mappedBy = "food")
	private List<CartItem> cartItems = new ArrayList<>();

	public Food(String name, int quantity, FoodStatus foodStatus) {
		this.name = name;
		this.quantity = quantity;
		this.foodStatus = foodStatus;
	}

	public void updateFood(String name, int quantity, FoodStatus foodStatus){
		this.name = name;
		this.quantity = quantity;
		this.foodStatus = foodStatus;
	}

	public void updateName(String name){
		this.name = name;
	}

	public void updateQuantity(int quantity){
		this.quantity = quantity;
	}

	public void updateFoodStatus(FoodStatus foodStatus){
		this.foodStatus = foodStatus;
	}
}
