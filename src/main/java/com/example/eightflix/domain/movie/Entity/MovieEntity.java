package com.example.eightflix.domain.movie.Entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Getter;

@Table(name = "tb_movie")
@Entity
@Getter
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;

    @Column(nullable = false, length = 20)
    private String name;

    public MovieEntity(String name) {
        this.name = name;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
