package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.RedisToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {
}
