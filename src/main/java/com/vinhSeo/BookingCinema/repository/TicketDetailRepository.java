package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.TicketDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketDetailRepository extends JpaRepository<TicketDetail, Integer> {

}
