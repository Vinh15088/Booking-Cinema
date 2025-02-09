package com.vinhSeo.BookingCinema.mapper;

import com.vinhSeo.BookingCinema.dto.request.MovieRequest;
import com.vinhSeo.BookingCinema.dto.response.MovieResponse;
import com.vinhSeo.BookingCinema.enums.MovieStatus;
import com.vinhSeo.BookingCinema.model.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    @Mapping(target = "release_date", expression = "java(buildStringToDate(request))")
    @Mapping(target = "status", expression = "java(buildStatus(request))")
    Movie toMovie(MovieRequest request);

    MovieResponse toMovieResponse(Movie movie);

    default Date buildStringToDate(MovieRequest request) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date releaseDate = dateFormat.parse(request.getRelease_date());

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

}
