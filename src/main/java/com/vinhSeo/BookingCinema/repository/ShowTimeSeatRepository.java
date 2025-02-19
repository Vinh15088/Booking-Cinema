package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.ShowTime;
import com.vinhSeo.BookingCinema.model.ShowTimeSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowTimeSeatRepository extends JpaRepository<ShowTimeSeat, Integer> {
    List<ShowTimeSeat> findAllByShowTime(ShowTime showTime);

}
