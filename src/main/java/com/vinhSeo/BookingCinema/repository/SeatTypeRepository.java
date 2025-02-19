package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatType, Integer> {

    @Query("SELECT st FROM SeatType st WHERE st.id = :seatTypeId")
    SeatType findBySeatTypeId(int seatTypeId);
}
