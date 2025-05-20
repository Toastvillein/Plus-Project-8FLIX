package com.example.eightflix.domain.user.repository;

import com.example.eightflix.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByUserId(String userId);
    Optional<User> findByUserId(String userId);
}
