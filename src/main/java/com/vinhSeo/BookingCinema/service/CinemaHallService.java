package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.CinemaHallRequest;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.CinemaHallMapper;
import com.vinhSeo.BookingCinema.model.Cinema;
import com.vinhSeo.BookingCinema.model.CinemaHall;
import com.vinhSeo.BookingCinema.model.Seat;
import com.vinhSeo.BookingCinema.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j(topic = "CINEMA_HALL_SERVICE")
@RequiredArgsConstructor
public class CinemaHallService {

    private final CinemaHallRepository cinemaHallRepository;
    private final CinemaRepository cinemaRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final CinemaHallMapper cinemaHallMapper;
    private final SeatRepository seatRepository;
    private final SeatTypeRepository seatTypeRepository;

    @Transactional
    protected void generateSeatsForShowTime(CinemaHall cinemaHall) {
        int rows = cinemaHall.getHallRow();
        int columns = cinemaHall.getHallColumn();

        for (int row = 0; row < rows; row++) {
            char rowChar = (char) ('A' + row);

            int seatTypeId;
            if (row < 4) seatTypeId = 1; // SeatType 1 (Standard)
            else if (row == rows - 1) seatTypeId = 3; // SeatType 3 (Couple)
            else seatTypeId = 2; // SeatType 2 (VIP)

            for (int col = 1; col <= columns; col++) {
                Seat seat = Seat.builder()
                        .seatNumber(rowChar + String.valueOf(col))
                        .seatStatus(true)
                        .seatType(seatTypeRepository.findBySeatTypeId(seatTypeId))
                        .cinemaHall(cinemaHall)
                        .build();
                seatRepository.save(seat);
            }
        }

        log.info("Generated {} seats in cinema hall {}", rows * columns, cinemaHall.getId());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Transactional
    public CinemaHall createCinemaHall(CinemaHallRequest request) {
        log.info("Create cinema hall");

        Cinema cinema = cinemaRepository.findById(request.getCinemaId()).orElseThrow(() ->
                new AppException(ErrorApp.CINEMA_NOT_FOUND));

        if(cinemaHallRepository.existsByNameAndCinema(request.getName(), cinema)) {
            log.info("Cinema hall already exists in cinema: {}", request.getCinemaId());
            throw new AppException(ErrorApp.CINEMA_HALL_EXISTED_IN_CINEMA);
        }

        CinemaHall cinemaHall = cinemaHallMapper.toCinemaHall(request, cinemaRepository, roomTypeRepository);

        cinemaHallRepository.save(cinemaHall);

        generateSeatsForShowTime(cinemaHall);

        return cinemaHall;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public CinemaHall getCinemaHallById(Integer id) {
        log.info("Get cinema hall by id: {}", id);

        return cinemaHallRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorApp.CINEMA_HALL_NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public List<CinemaHall> getAllCinemaHall(String keyword) {
        if(keyword != null && !keyword.isEmpty() && !keyword.isBlank()) {
            log.info("Get cinema hall by keyword: {}", keyword);

            return cinemaHallRepository.getAllCinemas(keyword);
        } else {
            log.info("Get all cinema hall");

            return cinemaHallRepository.findAll();
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Transactional
    public CinemaHall updateCinemaHall(Integer id, CinemaHallRequest request) {
        log.info("Update cinema hall");

        CinemaHall cinemaHall = getCinemaHallById(id);

        Cinema cinema = cinemaRepository.findById(request.getCinemaId()).orElseThrow(() ->
                new AppException(ErrorApp.CINEMA_NOT_FOUND));

        if(cinemaHallRepository.existsByNameAndCinema(request.getName(), cinema)) {
            log.info("Name & Cinema already exists in cinema: {}", request.getCinemaId());
            throw new AppException(ErrorApp.CINEMA_HALL_EXISTED_IN_CINEMA);
        }

        CinemaHall updatedCinemaHall = cinemaHallMapper.toCinemaHall(request, cinemaRepository, roomTypeRepository);
        updatedCinemaHall.setId(id);

        if(request.getHallRow() != getCinemaHallById(id).getHallRow() || request.getHallColumn() != getCinemaHallById(id).getHallColumn()) {
            seatRepository.deleteAllByCinemaHall(cinemaHall);

            generateSeatsForShowTime(cinemaHall);
        }

        updatedCinemaHall.setId(id);

        return cinemaHallRepository.save(updatedCinemaHall);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void deleteCinemaHall(Integer id) {
        log.info("Delete cinema hall");

        CinemaHall cinemaHall = getCinemaHallById(id);

        cinemaHallRepository.delete(cinemaHall);

    }
}
