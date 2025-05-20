package com.example.eightflix.domain.review.service;

import com.example.eightflix.domain.review.dto.ReviewRequest;
import com.example.eightflix.domain.review.dto.ReviewResponse;
import com.example.eightflix.domain.review.entity.Review;
import com.example.eightflix.domain.review.repository.ReviewRepository;
import com.example.eightflix.global.exception.BizException;
import com.example.eightflix.global.exception.CommonErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public ReviewResponse saveReview(Long userId, Long movieId, ReviewRequest request){

        //같은 유저가 같은 영화에 리뷰 한번 쓸 수 있음
        if(reviewRepository.existsReviewByUserIdAndMovieId(userId, movieId)){
            throw new BizException(CommonErrorCode.DUPLICATE_REVIEW);
        }
        Review review = Review.builder()
                .userId(userId)
                .movieId(movieId)
                .contents(request.contents())
                .rate(request.rate()).build();
        Review savedReview = reviewRepository.save(review);

        return ReviewResponse.from(savedReview);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> findReviews(Long movieId, Pageable pageable){
        return reviewRepository.findReviewByMovieId(movieId, pageable).map(ReviewResponse::from);
    }


    @Transactional
    public ReviewResponse patchReview(Long userId, Long movieId, Long reviewId, ReviewRequest request){
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->new BizException(CommonErrorCode.REVIEW_NOT_FOUND));
        if(!findReview.getUserId().equals(userId)){
            throw new BizException(CommonErrorCode.REVIEW_OWNER_MISMATCH);
        }
        findReview.updateReview(request);
        return ReviewResponse.from(findReview);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId){
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->null);
        if(!findReview.getUserId().equals(userId)){
            throw new BizException(CommonErrorCode.REVIEW_OWNER_MISMATCH);
        }
        findReview.softDelete();
    }
}
