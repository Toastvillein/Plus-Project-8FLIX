package com.example.eightflix.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eightflix.domain.reservation.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
