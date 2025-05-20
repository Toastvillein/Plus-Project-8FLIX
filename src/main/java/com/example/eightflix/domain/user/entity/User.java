package com.example.eightflix.domain.user.entity;

import com.example.eightflix.domain.food.entity.Cart;
import com.example.eightflix.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Builder
    public User(String nickname, String email, String userId, String password, String phoneNumber, UserRole role) {
        this.nickname = nickname;
        this.email = email;
        this.userId = userId;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.cart = new Cart();
    }
}
