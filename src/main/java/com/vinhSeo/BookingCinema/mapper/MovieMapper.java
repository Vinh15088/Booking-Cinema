package com.vinhSeo.BookingCinema.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vinhSeo.BookingCinema.dto.request.MovieRequest;
import com.vinhSeo.BookingCinema.dto.response.MovieResponse;
import com.vinhSeo.BookingCinema.enums.MovieStatus;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.model.Movie;
import com.vinhSeo.BookingCinema.model.MovieType;
import com.vinhSeo.BookingCinema.repository.MovieTypeRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    @Mapping(target = "releaseDate", expression = "java(buildStringToDate(request))")
    @Mapping(target = "status", expression = "java(buildStatus(request))")
    @Mapping(target = "movieType", expression = "java(buildMovieType(request, movieTypeRepository))")
    Movie toMovie(MovieRequest request, @Context MovieTypeRepository movieTypeRepository);

    @Mapping(target = "trailer", expression = "java(buildTrailer(movie))")
    @Mapping(target = "banner", expression = "java(buildBanner(movie))")
    @Mapping(target = "movieType", expression = "java(buildMovieTypeJson(movie))")
    MovieResponse toMovieResponse(Movie movie);

    default Date buildStringToDate(MovieRequest request) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date releaseDate = dateFormat.parse(request.getReleaseDate());

            return releaseDate;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd.");
        }
    }

    default MovieStatus buildStatus(MovieRequest request) {
        try {
            return MovieStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + request.getStatus());
        }
    }

    default MovieType buildMovieType(MovieRequest request, @Context MovieTypeRepository movieTypeRepository) {
        return movieTypeRepository.findById(request.getMovieType()).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));
    }

    default String buildTrailer(Movie movie) {
        return "https://res.cloudinary.com/dthlb2txt/image/upload/v1739962889/"
                + "BookingCinema/Trailer/" + movie.getId() + "/" + movie.getTrailer();
    }

    default String buildBanner(Movie movie) {
        return "https://res.cloudinary.com/dthlb2txt/image/upload/v1739962889/"
                + "BookingCinema/Banner/" + movie.getId() + "/" + movie.getBanner();
    }

    default JsonNode buildMovieTypeJson(Movie movie) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("movieTypeId", movie.getMovieType().getId());
        node.put("movieTypeName", movie.getMovieType().getName());

        return node;
    }

}
