package com.example.eightflix.domain.movie.MovieDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovieResponseDto {
    private Long movieId;
    private String name;
}