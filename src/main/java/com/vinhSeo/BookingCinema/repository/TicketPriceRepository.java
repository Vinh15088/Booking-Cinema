package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.TicketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketPriceRepository extends JpaRepository<TicketPrice, Integer> {

    Boolean existsTicketPriceByRoomTypeAndSeatType(Integer roomType, Integer seatType);

    TicketPrice findTicketPriceByRoomTypeAndSeatType(Integer roomType, Integer seatType);

}
