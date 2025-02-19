package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.CinemaHall;
import com.vinhSeo.BookingCinema.model.Seat;
import com.vinhSeo.BookingCinema.model.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    Boolean existsBySeatNumberAndCinemaHall(String seatNumber, CinemaHall cinemaHall);

    @Query("SELECT s FROM Seat s WHERE s.cinemaHall.id = :cinemaId")
    List<Seat> getAllSeatInCinemaHall(Integer cinemaId);

    void deleteAllByCinemaHall(CinemaHall cinemaHall);

    List<Seat> getAllByCinemaHall(CinemaHall cinemaHall);
}
