package com.example.eightflix.domain.user.repository;

import com.example.eightflix.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByUserId(String userId);
}
