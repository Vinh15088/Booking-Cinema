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
    @Mapping(target = "releaseDate", expression = "java(buildStringToDate(request))")
    @Mapping(target = "status", expression = "java(buildStatus(request))")
    Movie toMovie(MovieRequest request);

    @Mapping(target = "trailer", expression = "java(buildTrailer(movie))")
    @Mapping(target = "banner", expression = "java(buildBanner(movie))")
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

    default String buildTrailer(Movie movie) {
        return "https://res.cloudinary.com/dthlb2txt/image/upload/v1739962889/"
                + "BookingCinema/Trailer/" + movie.getId() + "/" + movie.getTrailer();
    }

    default String buildBanner(Movie movie) {
        return "https://res.cloudinary.com/dthlb2txt/image/upload/v1739962889/"
                + "BookingCinema/Banner/" + movie.getId() + "/" + movie.getBanner();
    }

}
