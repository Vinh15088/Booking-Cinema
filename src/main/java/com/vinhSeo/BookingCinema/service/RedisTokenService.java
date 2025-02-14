package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.exception.ResourceNotFoundException;
import com.vinhSeo.BookingCinema.model.RedisToken;
import com.vinhSeo.BookingCinema.repository.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "REDIS_TOKEN_SERVICE")
public class RedisTokenService {

    private final RedisTokenRepository redisTokenRepository;

    public void save(RedisToken redisToken) {
        log.info("save RedisToken: {}", redisToken.getId());

        redisTokenRepository.save(redisToken);
    }

    public boolean checkExisted(String id) {
        log.info("Check token existed: {}", id);

        if(!redisTokenRepository.existsById(id)) {
            throw new ResourceNotFoundException("Token not found");
        }

        return true;
    }

    public List<RedisToken> findAll() {
        log.info("Find all RedisTokens");

        return (List<RedisToken>) redisTokenRepository.findAll();
    }

    public void remove(String id) {
        log.info("remove RedisToken: {}", id);

        redisTokenRepository.deleteById(id);
    }

}
