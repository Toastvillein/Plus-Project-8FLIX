package com.example.eightflix.domain.review.controller;

import com.example.eightflix.domain.review.dto.ReviewRequest;
import com.example.eightflix.domain.review.dto.ReviewResponse;
import com.example.eightflix.domain.review.service.ReviewService;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    //리뷰 생성
    @PostMapping("movies/{movieId}/reviews")
    public ResponseEntity<ReviewResponse> saveReview(
            @PathVariable Long movieId,
            @RequestBody ReviewRequest request,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());
        ReviewResponse response = reviewService.saveReview(userId, movieId, request);
        return ResponseEntity.ok(response);
    }

    //리뷰 조회
    @GetMapping("movies/{movieId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> findReviews(
            @PathVariable Long movieId,
            @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable pageable
            ){
        Page<ReviewResponse> responses = reviewService.findReviews(movieId, pageable);
        return ResponseEntity.ok(responses);
    }

    //리뷰 수정
    @PatchMapping("movies/{movieId}/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> patchReview(
            @PathVariable Long movieId,
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest request,
            Authentication authentication
    ){
        Long userId = Long.valueOf(authentication.getName());
        ReviewResponse response = reviewService.patchReview(userId, movieId, reviewId, request);
        return ResponseEntity.ok(response);
    }

    //리뷰 삭제
    @DeleteMapping("movies/{moiveId}/reviews/{reviewid}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long moiveId,
            @PathVariable Long reviewid,
            Authentication authentication
    ){
        Long userId = Long.valueOf(authentication.getName());
        reviewService.deleteReview(userId, reviewid);
        return ResponseEntity.ok();
    }
}
