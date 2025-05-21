package com.example.eightflix.domain.reservation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.eightflix.domain.reservation.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	@Query("SELECT s FROM Seat s WHERE s.seatCode IN :seatCodes AND s.movie.movieId = :movieId")
	List<Seat> findValidSeatCodes(@Param("seatCodes") List<String> seatCodes, @Param("movieId") Long movieId);

	Optional<Seat> findBySeatCodeAndMovieMovieId(String seatCode, Long movieId);

}
