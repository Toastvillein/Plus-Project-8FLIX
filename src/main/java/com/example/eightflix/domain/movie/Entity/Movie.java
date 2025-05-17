package com.example.eightflix.domain.movie.Entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_movie")
@Entity
@Getter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;

    @Column(nullable = false, length = 20)
    private String name;

    public Movie(String name) {
        this.name = name;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
