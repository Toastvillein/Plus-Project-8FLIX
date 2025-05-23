package com.example.eightflix.domain.review.service;

import com.example.eightflix.domain.review.dto.RestPage;
import com.example.eightflix.domain.review.dto.ReviewRequest;
import com.example.eightflix.domain.review.dto.ReviewResponse;
import com.example.eightflix.domain.review.entity.Review;
import com.example.eightflix.domain.review.exception.ReviewErrorCode;
import com.example.eightflix.domain.review.repository.ReviewRepository;
import com.example.eightflix.global.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class ReviewRedisService {

    private final ReviewRepository reviewRepository;

    private final StringRedisTemplate redisTemplate;


    public void evictReviewsOfMovie(Long movieId) {
        Set<String> keys = redisTemplate.keys("reviewRedisCacheStore::movieReviews:" + movieId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public ReviewRedisService(ReviewRepository reviewRepository, StringRedisTemplate redisTemplate) {
        this.reviewRepository = reviewRepository;
        this.redisTemplate  = redisTemplate;
    }

    @Transactional
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
        evictReviewsOfMovie(movieId);
        return ReviewResponse.from(savedReview);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "reviewRedisCacheStore", key = "'movieReviews:' + #movieId + ':' + #pageable.pageNumber")
    public RestPage<ReviewResponse> findReviews(Long movieId, Pageable pageable){
        Page<Review> reviews = reviewRepository.findReviewByMovieId(movieId, pageable);
        return new RestPage<>(reviews.map(ReviewResponse::from));
    }


    @Transactional
    public ReviewResponse patchReview(Long userId, Long movieId, Long reviewId, ReviewRequest request){
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->new BizException(ReviewErrorCode.REVIEW_NOT_FOUND));
        if(!findReview.getUserId().equals(userId)){
            throw new BizException(ReviewErrorCode.REVIEW_OWNER_MISMATCH);
        }
        findReview.updateReview(request);
        evictReviewsOfMovie(movieId);
        return ReviewResponse.from(findReview);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId){
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->new BizException(ReviewErrorCode.REVIEW_NOT_FOUND));
        Long movieId = findReview.getMovieId();
        if(!findReview.getUserId().equals(userId)){
            throw new BizException(ReviewErrorCode.REVIEW_OWNER_MISMATCH);
        }
        findReview.softDelete();
        evictReviewsOfMovie(movieId);
    }
}
