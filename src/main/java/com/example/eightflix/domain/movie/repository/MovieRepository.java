package com.example.eightflix.domain.movie.repository;

import com.example.eightflix.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}