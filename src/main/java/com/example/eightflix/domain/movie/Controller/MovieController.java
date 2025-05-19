package com.example.eightflix.domain.movie.Controller;

import com.example.eightflix.domain.movie.MovieDto.MovieRequestDto;
import com.example.eightflix.domain.movie.MovieDto.MovieResponseDto;
import com.example.eightflix.domain.movie.Service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }
}