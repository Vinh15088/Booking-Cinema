package com.vinhSeo.BookingCinema.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vinhSeo.BookingCinema.dto.request.ShowTimeRequest;
import com.vinhSeo.BookingCinema.dto.response.ShowTimeResponse;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.model.CinemaHall;
import com.vinhSeo.BookingCinema.model.Movie;
import com.vinhSeo.BookingCinema.model.ShowTime;
import com.vinhSeo.BookingCinema.repository.CinemaHallRepository;
import com.vinhSeo.BookingCinema.repository.MovieRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShowTimeMapper {

    @Mapping(target = "movie", expression = "java(buildMovie(request, movieRepository))")
    @Mapping(target = "cinemaHall", expression = "java(buildCinemaHall(request, cinemaHallRepository))")
    ShowTime toShowTime(ShowTimeRequest request,
                        @Context MovieRepository movieRepository,
                        @Context CinemaHallRepository cinemaHallRepository);

    @Mapping(target = "movie", expression = "java(buildMovieJson(showTime))")
    @Mapping(target = "cinemaHall", expression = "java(buildCinemaHallJson(showTime))")
    ShowTimeResponse toShowTimeResponse(ShowTime showTime);

    default Movie buildMovie(ShowTimeRequest request, @Context MovieRepository movieRepository) {
        return movieRepository.findById(request.getMovieId()).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));
    }

    default CinemaHall buildCinemaHall(ShowTimeRequest request, @Context CinemaHallRepository cinemaHallRepository) {
        return cinemaHallRepository.findById(request.getCinemaHallId()).orElseThrow(() ->
                new AppException(ErrorApp.CINEMA_HALL_NOT_FOUND));
    }

    default JsonNode buildMovieJson(ShowTime showTime) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("movieId", showTime.getMovie().getId());
        node.put("movieTitle", showTime.getMovie().getTitle());
        node.put("movieStatus", showTime.getMovie().getStatus().ordinal());

        return node;
    }

    default JsonNode buildCinemaHallJson(ShowTime showTime) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("cinemaHallId", showTime.getCinemaHall().getId());
        node.put("cinemaHallName", showTime.getCinemaHall().getName());

        return node;
    }


}
