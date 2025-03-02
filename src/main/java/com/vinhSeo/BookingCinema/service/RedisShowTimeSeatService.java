package com.vinhSeo.BookingCinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j(topic = "REDIS_SHOWTIME_SEAT_SERVICE")
@RequiredArgsConstructor
public class RedisShowTimeSeatService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SHOW_TIME_SEAT_CACHE = "showtime_seat:";
    private static final Integer TTL = 10; // minutes

    public void setShowTimeSeatCache(Integer userId, Integer showTimeSeatId, String statusShowTimeSeat) {
        log.info("Push show time seat to cache");

        String key = SHOW_TIME_SEAT_CACHE + userId + ":" + showTimeSeatId;

        redisTemplate.opsForValue().set(key, statusShowTimeSeat, TTL, TimeUnit.MINUTES);
    }

    public String getShowTimeSeatCache(Integer userId, Integer showTimeSeatId) {
        log.info("Get show time seat from cache");

        String key = SHOW_TIME_SEAT_CACHE + userId + ":" + showTimeSeatId;

        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteAllHeldSeatsByUser(Integer userId) {
        log.info("Delete show time seat from cache");

        String pattern = SHOW_TIME_SEAT_CACHE + userId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);

        if(keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
