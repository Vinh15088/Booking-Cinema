package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.Cinema;
import com.vinhSeo.BookingCinema.model.CinemaHall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaHallRepository extends JpaRepository<CinemaHall, Integer> {
    Boolean existsByNameAndCinema(String name, Cinema cinema);

    @Query("SELECT c_h FROM CinemaHall c_h WHERE " +
            "c_h.name LIKE %:keyword% " +
            "OR c_h.cinema.address LIKE %:keyword% " +
            "OR c_h.cinema.city LIKE %:keyword% " +
            "OR c_h.roomType.name LIKE %:keyword%")
    List<CinemaHall> getAllCinemas(String keyword);
}
