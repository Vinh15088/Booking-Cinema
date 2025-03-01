package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.RedisTicket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisTicketRepository extends CrudRepository<RedisTicket, Integer> {

    RedisTicket findByBookingId(String bookingId);
}
