package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.CinemaRequest;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.CinemaMapper;
import com.vinhSeo.BookingCinema.model.Cinema;
import com.vinhSeo.BookingCinema.repository.CinemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j(topic = "CINEMA_SERVICE")
@RequiredArgsConstructor
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;

    public Cinema createNewCinema(CinemaRequest cinemaRequest) {
        log.info("Create new cinema");

        Cinema cinema = cinemaMapper.toCinema(cinemaRequest);

        return cinemaRepository.save(cinema);
    }

    public Cinema getCinemaById(Integer id) {
        log.info("Get cinema by id");

        return cinemaRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));
    }

    public List<Cinema> getAllCinemas() {
        log.info("Get all cinemas");

        return cinemaRepository.findAll();
    }

    public Cinema updateCinema(Integer id, CinemaRequest cinemaRequest) {
        log.info("Update cinema");

        Cinema cinema = getCinemaById(id);

        Cinema cinemaUpdate = cinemaMapper.toCinema(cinemaRequest);

        cinemaUpdate.setId(id);

        return cinemaRepository.save(cinemaUpdate);
    }

    public void deleteCinema(Integer id) {
        log.info("Delete cinema by id");

        if(!cinemaRepository.existsById(id)) {
            throw new AppException(ErrorApp.MOVIE_NOT_FOUND);
        }

        cinemaRepository.deleteById(id);
    }
}
