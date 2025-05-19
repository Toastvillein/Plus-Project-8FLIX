package com.example.eightflix.domain.review.entity;

import com.example.eightflix.domain.review.dto.ReviewRequest;
import com.example.eightflix.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@Table(name = "reviews")
@Getter
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    private Long userId;

    @JoinColumn(name = "movie_id")
    private Long movieId;

    @Column(nullable = false ,length = 300)
    private String contents;

    @Column(nullable = false)
    private Float rate;

    public void updateReview(ReviewRequest request){
        this.contents = request.contents();
        this.rate = request.rate();
    }

}
