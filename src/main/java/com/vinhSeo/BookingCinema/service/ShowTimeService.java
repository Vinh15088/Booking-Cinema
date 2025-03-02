package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.ShowTimeRequest;
import com.vinhSeo.BookingCinema.enums.MovieStatus;
import com.vinhSeo.BookingCinema.enums.ShowTimeSeatStatus;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.ShowTimeMapper;
import com.vinhSeo.BookingCinema.model.*;
import com.vinhSeo.BookingCinema.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j(topic = "SHOW_TIME_SERVICE")
@RequiredArgsConstructor
public class ShowTimeService {

    private final ShowTimeRepository showTimeRepository;
    private final ShowTimeMapper showTimeMapper;
    private final MovieRepository movieRepository;
    private final CinemaHallRepository cinemaHallRepository;
    private final SeatRepository seatRepository;
    private final ShowTimeSeatRepository showTimeSeatRepository;

    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Ho_Chi_Minh")
    // 0s 0m 3h *d *m
    public void cleanExpiredShowTimes() {
        log.info("Clean up expired show times");

        LocalDateTime now = LocalDateTime.now();

        List<ShowTime> expiredShowTimes = showTimeRepository.findByStartTimeBefore(now);

        if(expiredShowTimes.size() > 0) {
            log.info("Found {} expired show times", expiredShowTimes.size());

            showTimeRepository.deleteAll(expiredShowTimes);

            log.info("Deleted expired show times");
        } else {
            log.info("No expired show times found");
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public ShowTime createShowTime(ShowTimeRequest request) {
        log.info("Create new show time");

        Movie movie = movieRepository.findById(request.getMovieId()).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));

        if(movie.getStatus().equals(MovieStatus.ENDED)) {
            throw new AppException(ErrorApp.MOVIE_ENDED);
        }

        CinemaHall cinemaHall = cinemaHallRepository.findById(request.getCinemaHallId()).orElseThrow(() ->
                new AppException(ErrorApp.CINEMA_HALL_NOT_FOUND));

        if(cinemaHall.getAvailable().equals(false)) {
            throw new AppException(ErrorApp.CINEMA_HALL_NOT_AVAILABLE);
        }

        ShowTime showTime = showTimeMapper.toShowTime(request, movieRepository, cinemaHallRepository);

        if (showTimeRepository.existsByStartTimeAndMovieAndCinemaHallExceptCurrent(
                request.getStartTime(), movie, cinemaHall, showTime.getId())) {
            log.info("Show time already exists!");
            throw new AppException(ErrorApp.SHOW_TIME_EXISTED);
        }

        if (showTimeRepository.existsByOverlappingTimeExceptCurrent(
                request.getStartTime(), request.getEndTime(), cinemaHall, showTime.getId())) {
            log.info("Show time conflict");
            throw new AppException(ErrorApp.SHOW_TIME_CONFLICT);
        }

        showTime =  showTimeRepository.save(showTime);

        List<Seat> seatList = seatRepository.getAllByCinemaHall(cinemaHall);

        List<ShowTimeSeat> showTimeSeatList = new ArrayList<>();

        for (Seat seat : seatList) {
            ShowTimeSeat showTimeSeat = new ShowTimeSeat();
            showTimeSeat.setShowTimeSeatStatus(ShowTimeSeatStatus.AVAILABLE);
            showTimeSeat.setShowTime(showTime);
            showTimeSeat.setSeat(seat);
            showTimeSeatList.add(showTimeSeat);
        }

        showTimeSeatRepository.saveAll(showTimeSeatList);

        return showTime;
    }

    public ShowTime getShowTimeById(Integer id) {
        log.info("Get show time by id");

        return showTimeRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorApp.SHOW_TIME_NOT_FOUND));
    }

    public List<ShowTime> getShowTimesByMovie(Integer movieId) {
        log.info("Get show time by movie");

        Movie movie = movieRepository.findById(movieId).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));

        return showTimeRepository.getShowTimesByMovie(movie);
    }

    public ShowTime updateShowTime(Integer id, ShowTimeRequest request) {
        log.info("Update show time");

        ShowTime showTime = getShowTimeById(id);

        Movie movie = movieRepository.findById(request.getMovieId()).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));

        CinemaHall cinemaHall = cinemaHallRepository.findById(request.getCinemaHallId()).orElseThrow(() ->
                new AppException(ErrorApp.CINEMA_HALL_NOT_FOUND));

        if (showTimeRepository.existsByStartTimeAndMovieAndCinemaHallExceptCurrent(
                request.getStartTime(), movie, cinemaHall, id)) {
            log.info("Show time already exists!");
            throw new AppException(ErrorApp.SHOW_TIME_EXISTED);
        }

        if (showTimeRepository.existsByOverlappingTimeExceptCurrent(
                request.getStartTime(), request.getEndTime(), cinemaHall, id)) {
            log.info("Show time conflict");
            throw new AppException(ErrorApp.SHOW_TIME_CONFLICT);
        }

        ShowTime updatedShowTime = showTimeMapper.toShowTime(request, movieRepository, cinemaHallRepository);
        updatedShowTime.setId(id);

        return showTimeRepository.save(updatedShowTime);
    }

    public void deleteShowTime(Integer id) {
        log.info("Delete show time");

        ShowTime showTime = getShowTimeById(id);

        showTimeRepository.delete(showTime);
        log.info("Deleted show time and related seats successfully");
    }

}
