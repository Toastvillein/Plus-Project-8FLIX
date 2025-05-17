package com.example.eightflix.domain.movie.Service;

import com.example.eightflix.domain.movie.MovieDto.MovieRequestDto;
import com.example.eightflix.domain.movie.MovieDto.MovieResponseDto;
import com.example.eightflix.domain.movie.Entity.MovieEntity;
import com.example.eightflix.domain.movie.Repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieResponseDto createMovie(MovieRequestDto requestDto) {
        MovieEntity movie = new MovieEntity(requestDto.getName());
        MovieEntity saved = movieRepository.save(movie);
        return new MovieResponseDto(saved.getMovieId(), saved.getName());
    }

    public MovieResponseDto updateMovie(Long id, MovieRequestDto requestDto) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        movie.updateName(requestDto.getName());
        return new MovieResponseDto(movie.getMovieId(), movie.getName());
    }

    public List<MovieResponseDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(movie -> new MovieResponseDto(movie.getMovieId(), movie.getName()))
                .collect(Collectors.toList());
    }

    public MovieResponseDto getMovie(Long id) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
        return new MovieResponseDto(movie.getMovieId(), movie.getName());
    }

    public void deleteMovie(Long id) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
        movieRepository.delete(movie);
    }

}