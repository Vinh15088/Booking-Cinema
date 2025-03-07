package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.repository.BlackListTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j(topic = "BLACK_LIST_TOKEN_SERVICE")
@RequiredArgsConstructor
public class BlackListTokenService {

    private final BlackListTokenRepository blackListTokenRepository;

    @Scheduled(fixedRate = 1800000) // ms -> 30p
    public void blackListToken() {
        log.info("Automatically delete blacklist token after 30 minutes");

        Date now = new Date();
        blackListTokenRepository.deleteExpired(now);
    }
}
