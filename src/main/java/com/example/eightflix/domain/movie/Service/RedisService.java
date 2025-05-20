package com.example.eightflix.domain.movie.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String VIEW_COUNT_NUMBER = "movie:viewcount:";
    private static final String MOST_VIEWED_MOVIE_KEY = "movie:mostviewed";

    public void incrementViewCount(Long movieId) {
        String key = VIEW_COUNT_NUMBER + movieId;
        redisTemplate.opsForValue().increment(key, 1);
    }

    public Long getViewCount(Long movieId) {
        String key = VIEW_COUNT_NUMBER + movieId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }

    public String getTopMovieId() {
        return (String) redisTemplate.opsForValue().get(MOST_VIEWED_MOVIE_KEY);
    }

    public void updateTopMovie(Long currentMovieId) {
        Long currentCount = getViewCount(currentMovieId);

        String topMovieId = getTopMovieId();
        Long topCount = (topMovieId != null) ? getViewCount(Long.parseLong(topMovieId)) : 0L;

        if (topMovieId == null || currentCount > topCount) {
            redisTemplate.opsForValue().set(MOST_VIEWED_MOVIE_KEY, currentMovieId.toString());
        }
    }
}
