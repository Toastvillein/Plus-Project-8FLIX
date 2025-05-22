package com.example.eightflix.domain.movie.repository;

import com.example.eightflix.domain.movie.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findByNameContaining(String keyword, Pageable pageable);
}