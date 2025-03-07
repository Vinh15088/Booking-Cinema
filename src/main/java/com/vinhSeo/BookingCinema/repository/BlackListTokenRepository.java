package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface BlackListTokenRepository extends JpaRepository<BlackListToken, String> {

    @Transactional
    @Modifying
    @Query("DELETE FROM BlackListToken  b WHERE b.expiredAt < :now")
    void deleteExpired(Date now);
}
