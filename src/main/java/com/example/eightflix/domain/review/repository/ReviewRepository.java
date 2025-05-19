package com.example.eightflix.domain.review.repository;

import com.example.eightflix.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsReviewByUserIdAndMovieId(Long userId, Long movieId);

    Page<Review> findReviewByMovieId(Long movieId, Pageable pageable);
}
