package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.response.ShowTimeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j(topic = "SHOW_TIME_CACHE_SERVICE")
@RequiredArgsConstructor
public class ShowTimeCacheService {

    private static final String SHOW_TIME_CACHE_PREFIX = "showtime:";
    private static final Integer TTL = 60; // ttl in minutes

    private final RedisTemplate<String, Object> redisTemplate;

    public void cacheShowTime(Integer id, ShowTimeResponse response) {
        log.info("Push cached show time to redis");
        String key = SHOW_TIME_CACHE_PREFIX + id;

        redisTemplate.opsForValue().set(key, response, TTL, TimeUnit.MINUTES);
    }

    public ShowTimeResponse getCachedShowTime(Integer id) {
        log.info("Get cached show time for id {}", id);
        String key = SHOW_TIME_CACHE_PREFIX + id;

        return (ShowTimeResponse) redisTemplate.opsForValue().get(key);
    }

    public void cacheShowTimeByMovie(Integer movieId, List<ShowTimeResponse> responses) {
        log.info("Push cached show time for movie {}", movieId);

        String key = SHOW_TIME_CACHE_PREFIX + "movie:" + movieId;

        redisTemplate.opsForValue().set(key, responses, TTL, TimeUnit.MINUTES);
    }

    public List<ShowTimeResponse> getCachedShowTimesByMovie(Integer movieId) {
        log.info("Get cached show time for movie {}", movieId);

        String key = SHOW_TIME_CACHE_PREFIX + "movie:" + movieId;

        return (List<ShowTimeResponse>) redisTemplate.opsForValue().get(key);
    }

    public void evictShowTimeCache(Integer id) {
        log.info("Remove cached show time from redis");

        String key = SHOW_TIME_CACHE_PREFIX + id;

        redisTemplate.delete(key);
    }

    public void evictShowTimesByMovie(Integer movieId) {
        String key = SHOW_TIME_CACHE_PREFIX + "movie:" + movieId;

        redisTemplate.delete(key);
    }
}
