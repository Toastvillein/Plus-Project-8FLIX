package com.example.eightflix.domain.movie.Repository;

import com.example.eightflix.domain.movie.Entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<MovieEntity, Long> {
}