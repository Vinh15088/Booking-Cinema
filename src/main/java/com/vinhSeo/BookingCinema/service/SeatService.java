package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.SeatRequest;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.SeatMapper;
import com.vinhSeo.BookingCinema.model.CinemaHall;
import com.vinhSeo.BookingCinema.model.Seat;
import com.vinhSeo.BookingCinema.repository.CinemaHallRepository;
import com.vinhSeo.BookingCinema.repository.SeatRepository;
import com.vinhSeo.BookingCinema.repository.SeatTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j(topic = "SEAT_SERVICE")
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final SeatTypeRepository seatTypeRepository;
    private final CinemaHallRepository cinemaHallRepository;
    private final SeatMapper seatMapper;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Seat createNewSeat(SeatRequest seatRequest) {
        log.info("Create new seat");

        CinemaHall cinemaHall = cinemaHallRepository.findById(seatRequest.getCinemaHallId()).orElseThrow(() ->
                new AppException(ErrorApp.CINEMA_HALL_NOT_FOUND));

        if(seatRepository.existsBySeatNumberAndCinemaHall(seatRequest.getSeatNumber(), cinemaHall)) {
            log.info("Seat already exists in Cinema hall: {}", seatRequest.getCinemaHallId());
            throw new AppException(ErrorApp.SEAT_EXISTED_IN_CINEMA_HALL);
        }

        Seat seat = seatMapper.toSeat(seatRequest, seatTypeRepository, cinemaHallRepository);

        return seatRepository.save(seat);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public Seat getSeatById(Integer seatId) {
        log.info("Get seat by id: {}", seatId);

        return seatRepository.findById(seatId).orElseThrow(() ->
                new AppException(ErrorApp.SEAT_NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public List<Seat> getAllSeatsInCinemaHall(Integer cinemaHallId) {
        log.info("Get all seats in Cinema hall: {}", cinemaHallId);

        return seatRepository.getAllSeatInCinemaHall(cinemaHallId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Seat updateSeat(Integer seatId, SeatRequest seatRequest) {
        log.info("Update seat by id: {}", seatId);

        Seat seat = getSeatById(seatId);

        CinemaHall cinemaHall = cinemaHallRepository.findById(seatRequest.getCinemaHallId()).orElseThrow(() ->
                new AppException(ErrorApp.CINEMA_HALL_NOT_FOUND));

        if(seatRepository.existsBySeatNumberAndCinemaHall(seatRequest.getSeatNumber(), cinemaHall)) {
            log.info("Seat name existed in: {}", seatRequest.getCinemaHallId());
            throw new AppException(ErrorApp.SEAT_EXISTED_IN_CINEMA_HALL);
        }

        Seat updatedSeat = seatMapper.toSeat(seatRequest, seatTypeRepository, cinemaHallRepository);

        updatedSeat.setId(seatId);

        return seatRepository.save(updatedSeat);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public Seat changeSeatStatus(Integer seatId) {
        log.info("Change seat status by id: {}", seatId);

        Seat seat = getSeatById(seatId);

        seat.setSeatStatus(!seat.getSeatStatus());

        return seatRepository.save(seat);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void deleteSeat(Integer seatId) {
        log.info("Delete seat by id: {}", seatId);

        Seat seat = getSeatById(seatId);

        seatRepository.delete(seat);
    }

}
