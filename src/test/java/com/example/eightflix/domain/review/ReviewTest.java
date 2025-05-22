package com.example.eightflix.domain.review;

import com.example.eightflix.domain.review.dto.ReviewRequest;
import com.example.eightflix.domain.review.dto.ReviewResponse;
import com.example.eightflix.domain.review.entity.Review;
import com.example.eightflix.domain.review.exception.ReviewErrorCode;
import com.example.eightflix.domain.review.repository.ReviewRepository;
import com.example.eightflix.domain.review.service.ReviewCacheService;
import com.example.eightflix.domain.review.service.ReviewRedisService;
import com.example.eightflix.domain.review.service.ReviewService;
import com.example.eightflix.global.exception.BizException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@Transactional
@Sql("classpath:/review_test_db.sql")
@ActiveProfiles("test")
public class ReviewTest {

    @Autowired
    private ReviewService reviewService;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewCacheService reviewCacheService;
    @Autowired
    private ReviewRedisService reviewRedisService;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
    }
//    @BeforeEach
//    void setUp() {
//        System.out.println("셋업");
//        ReviewRequest request = new ReviewRequest("테스트 리뷰", 4.0f);
//        Long userId = 1L;
//        Long movieId = 1L;
//
//        ReviewResponse response = reviewService.saveReview(userId, movieId, request);
//        System.out.println(response.reviewId());
//    }

    @Test
    void 영화별리뷰조회(){
        Long movieId = 1L;
        int page = 0;
        int size = 10;
        Sort.Direction direction = Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page,size, Sort.by(direction, "createdAt"));
        Page<ReviewResponse> responses = reviewService.findReviews(movieId, pageable);
        assertThat(responses.getTotalElements()).isEqualTo(1L);
    }


    @Test
    void 리뷰_저장하기(){
        ReviewRequest request = new ReviewRequest("재미있었어요", 4.5f);

        Long userId = 1L;
        Long movieId = 2L;
        //리뷰 저장
        ReviewResponse response = reviewService.saveReview(userId, movieId, request);

        // 4. 검증
        assertThat(response).isNotNull();
        assertThat(response.reviewId()).isEqualTo(2L);
        assertThat(response.contents()).isEqualTo("재미있었어요");
        assertThat(response.rate()).isEqualTo(4.5f);
        assertThat(response.userId()).isEqualTo(userId);
    }

    @Test
    void 한영화에_여러번_리뷰는_못쓴다(){
        Long userId = 1L;
        Long movieId = 1L;
        ReviewRequest request = new ReviewRequest("재미있었어요", 4.5f);
        //리뷰 저장
        BizException exception = assertThrows(BizException.class, () -> {
            reviewService.saveReview(userId, movieId, request);
        });

        // 예외 코드도 검증 (옵션)
        assertThat(exception.getErrorCode()).isEqualTo(ReviewErrorCode.DUPLICATE_REVIEW);
    }

    @Test
    void 리뷰_수정하기() {
        Long userId = 1L;
        Long movieId = 1L;
        Long reviewId = 1L;

        ReviewRequest updateRequest = new ReviewRequest("수정된 리뷰입니다", 3.0f);
        ReviewResponse updated = reviewService.patchReview(userId, movieId, reviewId, updateRequest);

        assertThat(updated).isNotNull();
        assertThat(updated.contents()).isEqualTo("수정된 리뷰입니다");
        assertThat(updated.rate()).isEqualTo(3.0f);
        assertThat(updated.userId()).isEqualTo(userId);
    }
    @Test
    void 리뷰는나만수정할수있다() {
        Long userId = 2L;
        Long movieId = 1L;
        Long reviewId = 1L;

        ReviewRequest updateRequest = new ReviewRequest("수정된 리뷰입니다", 3.0f);
        BizException exception=assertThrows(BizException.class, () -> {
            reviewService.patchReview(userId, movieId, reviewId, updateRequest);
        });
        assertThat(exception.getErrorCode()).isEqualTo(ReviewErrorCode.REVIEW_OWNER_MISMATCH);
    }


    @Test
    void 리뷰_삭제하기() {
        Long userId = 1L;
        Long reviewId = 1L;

        reviewService.deleteReview(userId, reviewId);
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        assertThat(optionalReview.get().getDeletedAt()).isNotNull();
    }

    @Test
    void 리뷰는_나만_삭제하기() {
        Long userId = 2L;
        Long reviewId = 1L;

        BizException exception = assertThrows(BizException.class,
                ()->{reviewService.deleteReview(userId, reviewId);});
        assertThat(exception.getErrorCode()).isEqualTo(ReviewErrorCode.REVIEW_OWNER_MISMATCH);

    }



    @Test
    void 캐시적용하면빠를까(){
        Long movieId = 1L;
        int page = 0;
        int size = 10;
        Sort.Direction direction = Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page,size, Sort.by(direction, "createdAt"));
        //캐시적용안함
        long start = System.currentTimeMillis();
        Page<ReviewResponse> responses = reviewService.findReviews(movieId, pageable);
        long end = System.currentTimeMillis();

        //로컬캐시적용
        reviewCacheService.findReviews(movieId, pageable);
        long startCache = System.currentTimeMillis();
        reviewCacheService.findReviews(movieId, pageable);
        long endCache = System.currentTimeMillis();

        //레디스 적용
        reviewRedisService.findReviews(movieId, pageable);
        long startRedisCache = System.currentTimeMillis();
        reviewRedisService.findReviews(movieId, pageable);
        long endRedisCache = System.currentTimeMillis();
        System.out.println("기본 : " + (end - start));
        System.out.println("로컬 : " + (endCache - startCache));
        System.out.println("레디스 : " + (endRedisCache - startRedisCache));


        assertTrue(end - start > endCache - startCache);
        assertTrue(end - start > endRedisCache - startRedisCache);

    }
}