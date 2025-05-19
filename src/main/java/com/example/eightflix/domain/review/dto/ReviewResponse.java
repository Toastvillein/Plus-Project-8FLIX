package com.example.eightflix.domain.review.dto;


import com.example.eightflix.domain.review.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long reviewId,
        String contents,
        Float rate,
        Long userId,
        Long movieId,
        LocalDateTime createAt,
        LocalDateTime modifiedAt

) {

    public static ReviewResponse from(Review review){
        return new ReviewResponse(
                review.getId(),
                review.getContents(),
                review.getRate(),
                review.getUserId(),
                review.getMovieId(),
                review.getCreatedAt(),
                review.getModifiedAt()
        );
    }
}
