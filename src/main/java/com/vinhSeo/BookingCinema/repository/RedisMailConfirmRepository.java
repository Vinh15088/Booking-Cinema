package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.RedisMailConfirm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisMailConfirmRepository extends CrudRepository<RedisMailConfirm, String> {
}
