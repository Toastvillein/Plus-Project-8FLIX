package com.example.eightflix.domain.movie.controller;

import com.example.eightflix.domain.movie.dto.MovieRequestDto;
import com.example.eightflix.domain.movie.dto.MovieResponseDto;
import com.example.eightflix.domain.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public MovieResponseDto createMovie(@RequestBody MovieRequestDto requestDto) {
        return movieService.createMovie(requestDto);
    }

    @PutMapping("/{id}")
    public MovieResponseDto updateMovie(@PathVariable Long id, @RequestBody MovieRequestDto requestDto) {
        return movieService.updateMovie(id, requestDto);
    }

    @GetMapping
    public List<MovieResponseDto> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public MovieResponseDto getMovie(@PathVariable Long id) {
        return movieService.getMovie(id);
    }

    @GetMapping("/v1/search")
    public List<MovieResponseDto> searchV1(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return movieService.searchMoviesV1(keyword, page, size);
    }

    @GetMapping("/v2/search")
    public List<MovieResponseDto> searchV2(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return movieService.searchMoviesV2(keyword, page, size);
    }

    @GetMapping("/search/top")
    public ResponseEntity<Set<String>> getTopSearchKeywords(@RequestParam(defaultValue = "10") int limit) {
        Set<String> keywords = movieService.getTopKeywords(limit);
        return ResponseEntity.ok(keywords);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }
}