package com.example.eightflix.domain.review.service;

import com.example.eightflix.domain.review.dto.ReviewRequest;
import com.example.eightflix.domain.review.dto.ReviewResponse;
import com.example.eightflix.domain.review.entity.Review;
import com.example.eightflix.domain.review.exception.ReviewErrorCode;
import com.example.eightflix.domain.review.repository.ReviewRepository;
import com.example.eightflix.global.exception.BizException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CacheConfig(cacheManager = "reviewCacheManager")  // 이 서비스는 메모리 캐시를 사용함
public class ReviewCacheService {

    private final ReviewRepository reviewRepository;

    public ReviewCacheService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    @CacheEvict(value = "reviewCacheStore", key = "'movieReviews:' + #movieId + ':0'")
    public ReviewResponse saveReview(Long userId, Long movieId, ReviewRequest request){

        //같은 유저가 같은 영화에 리뷰 한번 쓸 수 있음
        if(reviewRepository.existsReviewByUserIdAndMovieId(userId, movieId)){
            throw new BizException(ReviewErrorCode.DUPLICATE_REVIEW);
        }
        Review review = Review.builder()
                .userId(userId)
                .movieId(movieId)
                .contents(request.contents())
                .rate(request.rate()).build();
        Review savedReview = reviewRepository.save(review);

        return ReviewResponse.from(savedReview);
    }

    @Cacheable(value = "reviewCacheStore", key = "'movieReviews:' + #movieId + ':' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<ReviewResponse> findReviews(Long movieId, Pageable pageable){
        return reviewRepository.findReviewByMovieId(movieId, pageable).map(ReviewResponse::from);
    }


    @Transactional
    public ReviewResponse patchReview(Long userId, Long movieId, Long reviewId, ReviewRequest request){
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->new BizException(ReviewErrorCode.REVIEW_NOT_FOUND));
        if(!findReview.getUserId().equals(userId)){
            throw new BizException(ReviewErrorCode.REVIEW_OWNER_MISMATCH);
        }
        findReview.updateReview(request);
        return ReviewResponse.from(findReview);
    }

    @Transactional
    @CacheEvict(value = "reviewCacheStore", allEntries = true)
    public void deleteReview(Long userId, Long reviewId){
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->new BizException(ReviewErrorCode.REVIEW_NOT_FOUND));
        if(!findReview.getUserId().equals(userId)){
            throw new BizException(ReviewErrorCode.REVIEW_OWNER_MISMATCH);
        }
        findReview.softDelete();
    }
}
