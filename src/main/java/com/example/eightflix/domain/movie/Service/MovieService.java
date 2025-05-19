package com.example.eightflix.domain.movie.Service;

import com.example.eightflix.domain.movie.MovieDto.MovieRequestDto;
import com.example.eightflix.domain.movie.MovieDto.MovieResponseDto;
import com.example.eightflix.domain.movie.Entity.Movie;
import com.example.eightflix.domain.movie.Repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String VIEW_COUNT_KEY = "movie:viewcount";
    private static final String MOST_VIEWED_MOVIE_KEY = "movie:mostviewed";

    @Transactional
    public MovieResponseDto createMovie(MovieRequestDto requestDto) {
        Movie movie = new Movie(requestDto.getName());
        Movie saved = movieRepository.save(movie);
        return new MovieResponseDto(saved.getMovieId(), saved.getName());
    }

    @Transactional
    //캐시무효화
    @CacheEvict(value = "movie", key = "#id")
    public MovieResponseDto updateMovie(Long id, MovieRequestDto requestDto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        movie.updateName(requestDto.getName());
        return new MovieResponseDto(movie.getMovieId(), movie.getName());
    }
//z정렬 없애기
//redis동작 서비스말고 딴데 구현해서 호출
//캐싱작업우선적으로 하기
    @Cacheable(value = "movie::all")
    public List<MovieResponseDto> getAllMovies() {
        List<Movie> result = new ArrayList<>();

        // 가장 많이 본 영화 1개 먼저 꺼냄
        String topIdStr = (String) redisTemplate.opsForValue().get(MOST_VIEWED_MOVIE_KEY);

        if (topIdStr != null) {
            Long topId = Long.parseLong(topIdStr);
            movieRepository.findById(topId).ifPresent(result::add);
        }

        // 나머지 전체 목록 가져오기
        List<Movie> all = movieRepository.findAll();

        // 중복 제거 후 추가
        for (Movie m : all) {
            if (result.stream().noneMatch(r -> r.getMovieId().equals(m.getMovieId()))) {
                result.add(m);
            }
        }

        return result.stream()
                .map(m -> new MovieResponseDto(m.getMovieId(), m.getName()))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "movie", key = "#id")
    public MovieResponseDto getMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        Double score = redisTemplate.opsForZSet().incrementScore(VIEW_COUNT_KEY, id.toString(), 1);

        String currentTopId = (String) redisTemplate.opsForValue().get(MOST_VIEWED_MOVIE_KEY);
        Double currentTopScore = currentTopId != null ? redisTemplate.opsForZSet().score(VIEW_COUNT_KEY, currentTopId) : 0.0;

        if (currentTopId == null || (score != null && score > currentTopScore)) {
            redisTemplate.opsForValue().set(MOST_VIEWED_MOVIE_KEY, id.toString());
        }

        return new MovieResponseDto(movie.getMovieId(), movie.getName());
    }

    @Transactional
    @CacheEvict(value = "movie", key = "#id")
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
        movieRepository.delete(movie);
    }

}