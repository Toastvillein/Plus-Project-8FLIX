package com.example.eightflix.movie;

import com.example.eightflix.domain.movie.entity.Movie;
import com.example.eightflix.domain.movie.repository.MovieRepository;
import com.example.eightflix.domain.movie.service.MovieService;
import com.example.eightflix.domain.movie.service.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class MovieTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private MovieService movieService;

    private Movie movie1;

    @BeforeEach
    void setup() {
        movie1 = new Movie("더미영화 1");
    }

    @Test
    void searchMoviesV2_cacheTest() {
        int total = 500;
        Pageable pageable = PageRequest.of(0, 10);

        List<Movie> dummyList = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            dummyList.add(new Movie("더미영화 " + i));
        }

        Page<Movie> moviePage = new PageImpl<>(dummyList.subList(0, 10), pageable, total);
        when(movieRepository.findByNameContaining("더미", pageable)).thenReturn(moviePage);

        // 첫 번째 호출 - DB 조회
        long start1 = System.nanoTime();
        movieService.searchMoviesV2("더미", 0, 10);
        long end1 = System.nanoTime();

        // 두 번째 호출 - 캐시 조회
        long start2 = System.nanoTime();
        movieService.searchMoviesV2("더미", 0, 10);
        long end2 = System.nanoTime();

        System.out.println("첫 번째 호출 시간(ms): " + (end1 - start1) / 1_000_000);
        System.out.println("두 번째 호출 시간(ms): " + (end2 - start2) / 1_000_000);

        verify(movieRepository, times(2)).findByNameContaining("더미", pageable);
    }



}
