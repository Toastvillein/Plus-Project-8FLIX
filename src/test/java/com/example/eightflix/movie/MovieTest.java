package com.example.eightflix.movie;

import com.example.eightflix.domain.movie.entity.Movie;
import com.example.eightflix.domain.movie.repository.MovieRepository;
import com.example.eightflix.domain.movie.service.MovieService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = "spring.cache.type=redis") // 캐시 적용 확인
public class MovieTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @BeforeAll
    void insertDummyDataOnce() {
        if (movieRepository.count() >= 50000) return;

        List<Movie> movies = new ArrayList<>();
        for (int i = 1; i <= 50000; i++) {
            movies.add(new Movie("더미영화 " + i));
        }
        movieRepository.saveAll(movies);
    }

    @Test
    @Order(1)
    void searchMoviesV1_performanceTest() {
        long start = System.currentTimeMillis();

        movieService.searchMoviesV1("더미", 0, 10);

        long end = System.currentTimeMillis();
        System.out.println("🔍 V1 실행 시간: " + (end - start) + "ms");
    }

    @Test
    @Order(2)
    void searchMoviesV2_performanceTest_first() {
        // 첫 요청: 캐시 미적용 (warm-up)
        movieService.searchMoviesV2("더미", 0, 10);
    }

    @Test
    @Order(3)
    void searchMoviesV2_performanceTest_cached() {
        long start = System.currentTimeMillis();

        movieService.searchMoviesV2("더미", 0, 10);

        long end = System.currentTimeMillis();
        System.out.println("⚡ V2 (캐시) 실행 시간: " + (end - start) + "ms");
    }
}
