package com.example.eightflix.domain.movie.service;

import com.example.eightflix.domain.movie.dto.MovieRequestDto;
import com.example.eightflix.domain.movie.dto.MovieResponseDto;
import com.example.eightflix.domain.movie.entity.Movie;
import com.example.eightflix.domain.movie.repository.MovieRepository;
import com.example.eightflix.domain.movie.exception.MovieErrorCode;
import com.example.eightflix.global.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final RedisService redisService;

    @Transactional
    public MovieResponseDto createMovie(MovieRequestDto requestDto) {
        Movie movie = new Movie(requestDto.getName());
        Movie saved = movieRepository.save(movie);
        return new MovieResponseDto(saved.getMovieId(), saved.getName());
    }

    @Transactional
    @CacheEvict(value = "movie", allEntries = true)
    public MovieResponseDto updateMovie(Long id, MovieRequestDto requestDto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new BizException(MovieErrorCode.MOVIE_NOT_FOUND));

        movie.updateName(requestDto.getName());
        return new MovieResponseDto(movie.getMovieId(), movie.getName());
    }

    @Cacheable(value = "movie", key = "'all'")
    public List<MovieResponseDto> getAllMovies() {
        List<Movie> result = new ArrayList<>();

        // 가장 많이 본 영화 ID 가져오기
        String topIdStr = redisService.getTopMovieId();

        if (topIdStr != null) {
            try {
                Long topId = Long.parseLong(topIdStr);
                movieRepository.findById(topId).ifPresent(result::add);
            } catch (NumberFormatException e) {
                // 잘못된 값이 Redis에 저장되어 있을 경우 무시
            }
        }

        // 나머지 전체 영화 목록
        List<Movie> all = movieRepository.findAll();

        // 중복 제거하고 추가
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
                .orElseThrow(() -> new BizException(MovieErrorCode.MOVIE_NOT_FOUND));

        // 조회수 증가 및 최고 조회수 영화 갱신
        redisService.incrementViewCount(id);
        redisService.updateTopMovie(id);

        return new MovieResponseDto(movie.getMovieId(), movie.getName());
    }

    // V1: 캐시 없음
    public List<MovieResponseDto> searchMoviesV1(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> result = movieRepository.findByNameContaining(keyword, pageable);
        redisService.incrementKeywordSearchCount(keyword); // 인기 검색어 집계는 유지
        return result.getContent().stream()
                .map(m -> new MovieResponseDto(m.getMovieId(), m.getName()))
                .collect(Collectors.toList());
    }

    // V2: Local Memory Cache 적용
    @Cacheable(value = "searchCache", key = "#keyword + ':' + #page + ':' + #size")
    public List<MovieResponseDto> searchMoviesV2(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> result = movieRepository.findByNameContaining(keyword, pageable);
        redisService.incrementKeywordSearchCount(keyword);
        return result.getContent().stream()
                .map(m -> new MovieResponseDto(m.getMovieId(), m.getName()))
                .collect(Collectors.toList());
    }

    public Set<String> getTopKeywords(int limit) {
        return redisService.getTopKeywords(limit);
    }

    @Transactional
    @CacheEvict(value = "movie", key = "#id")
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new BizException(MovieErrorCode.MOVIE_NOT_FOUND));
        movieRepository.delete(movie);
    }

}
