package com.example.eightflix.domain.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovieResponseDto {
    private Long movieId;
    private String name;
}