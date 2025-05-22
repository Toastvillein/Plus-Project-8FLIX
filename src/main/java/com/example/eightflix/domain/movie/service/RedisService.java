package com.example.eightflix.domain.movie.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String VIEW_COUNT_NUMBER = "movie:viewcount:";
    private static final String MOST_VIEWED_MOVIE_KEY = "movie:mostviewed";
    private static final String SEARCH_KEYWORD_RANKING = "search:ranking";

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
        Object result = redisTemplate.opsForValue().get(MOST_VIEWED_MOVIE_KEY);
        return result != null ? result.toString() : null;
    }

    public void updateTopMovie(Long currentMovieId) {
        Long currentCount = getViewCount(currentMovieId);

        String topMovieId = getTopMovieId();
        Long topCount = (topMovieId != null) ? getViewCount(Long.parseLong(topMovieId)) : 0L;

        if (topMovieId == null || currentCount > topCount) {
            redisTemplate.opsForValue().set(MOST_VIEWED_MOVIE_KEY, currentMovieId.toString());
        }
    }

    public void incrementKeywordSearchCount(String keyword) {
        redisTemplate.opsForZSet().incrementScore(SEARCH_KEYWORD_RANKING, keyword, 1);
    }

    public Set<String> getTopKeywords(int limit) {
        Set<Object> rawSet = redisTemplate.opsForZSet()
                .reverseRange(SEARCH_KEYWORD_RANKING, 0, limit - 1);

        // Object → String
        return rawSet.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
}
