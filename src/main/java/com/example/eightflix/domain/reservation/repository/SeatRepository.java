package com.example.eightflix.domain.reservation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.example.eightflix.domain.reservation.entity.Seat;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	@Query("SELECT s FROM Seat s WHERE s.seatCode IN :seatCodes AND s.movie.movieId = :movieId")
	List<Seat> findValidSeatCodes(@Param("seatCodes") List<String> seatCodes, @Param("movieId") Long movieId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="10000")})
	@Query("SELECT s FROM Seat s WHERE s.seatCode IN :seatCodes AND s.movie.movieId = :movieId")
	List<Seat> findValidSeatCodesWithLock(@Param("seatCodes") List<String> seatCodes, @Param("movieId") Long movieId);

	Optional<Seat> findBySeatCodeAndMovieMovieId(String seatCode, Long movieId);

}
