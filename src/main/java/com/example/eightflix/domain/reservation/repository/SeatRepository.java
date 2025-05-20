package com.example.eightflix.domain.reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.eightflix.domain.reservation.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	@Query("SELECT s FROM Seat s WHERE s.seatCode IN :seatCodes AND s.movie = :movieId")
	List<Seat> findValidSeatCodes(List<String> seatCodes, Long movieId);

}
