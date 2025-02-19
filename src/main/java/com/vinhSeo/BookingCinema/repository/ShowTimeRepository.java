package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.CinemaHall;
import com.vinhSeo.BookingCinema.model.Movie;
import com.vinhSeo.BookingCinema.model.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Integer> {
    @Query("SELECT CASE WHEN COUNT(st) > 0 THEN true ELSE false END FROM ShowTime st WHERE " +
            "st.startTime = :startTime " +
            "AND st.movie = :movie " +
            "AND st.cinemaHall = :cinemaHall " +
            "AND st.id <> :id")
    boolean existsByStartTimeAndMovieAndCinemaHallExceptCurrent(
            @Param("startTime") Date startTime,
            @Param("movie") Movie movie,
            @Param("cinemaHall") CinemaHall cinemaHall,
            @Param("id") Integer id
    );

    @Query("SELECT CASE WHEN COUNT(st) > 0 THEN true ELSE false END FROM ShowTime st WHERE " +
            "st.cinemaHall = :cinemaHall " +
            "AND (" +
            "(:startTime BETWEEN st.startTime AND st.endTime) " +
            "OR (:endTime BETWEEN st.startTime AND st.endTime) " +
            "OR (st.startTime BETWEEN :startTime AND :endTime) " +
            "OR (st.endTime BETWEEN :startTime AND :endTime)" +
            ") " +
            "AND st.id <> :id")
    boolean existsByOverlappingTimeExceptCurrent(
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("cinemaHall") CinemaHall cinemaHall,
            @Param("id") Integer id
    );

    List<ShowTime> getShowTimesByMovie(Movie movie);

    @Query("SELECT st FROM ShowTime st WHERE st.startTime < :checkTime")
    List<ShowTime> findByStartTimeBefore(@Param("checkTime") LocalDateTime checkTime);
}
