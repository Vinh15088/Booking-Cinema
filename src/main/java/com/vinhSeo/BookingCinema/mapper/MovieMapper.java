package com.vinhSeo.BookingCinema.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vinhSeo.BookingCinema.dto.request.MovieRequest;
import com.vinhSeo.BookingCinema.dto.response.MovieResponse;
import com.vinhSeo.BookingCinema.enums.MovieStatus;
import com.vinhSeo.BookingCinema.model.Movie;
import com.vinhSeo.BookingCinema.model.MovieHasMovieType;
import com.vinhSeo.BookingCinema.model.MovieType;
import com.vinhSeo.BookingCinema.repository.MovieTypeRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    @Mapping(target = "releaseDate", expression = "java(buildStringToDate(request))")
    @Mapping(target = "status", expression = "java(buildStatus(request))")
    @Mapping(target = "movieHasMovieTypes", expression = "java(buildMovieHasMovieType(request, movieTypeRepository))")
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

    default List<MovieHasMovieType> buildMovieHasMovieType(MovieRequest request, @Context MovieTypeRepository movieTypeRepository) {
        List<MovieHasMovieType> movieHasMovieTypes = new ArrayList<>();

        List<MovieType> movieTypes = movieTypeRepository.findAllById(request.getMovieType());

        for (MovieType movieType : movieTypes) {
            MovieHasMovieType movieHasMovieType = MovieHasMovieType.builder()
                    .movieType(movieType)
                    .build();

            movieHasMovieTypes.add(movieHasMovieType);
        }
        return movieHasMovieTypes;
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
        ArrayNode arrayNode = mapper.createArrayNode();

        List<MovieHasMovieType> movieHasMovieTypes = movie.getMovieHasMovieTypes();

        for(MovieHasMovieType movieHasMovieType : movieHasMovieTypes) {
            ObjectNode movieTypeJson = mapper.createObjectNode();

            movieTypeJson.put("id", movieHasMovieType.getMovieType().getId());
            String typeName = movieHasMovieType.getMovieType().getName();
            movieTypeJson.put("name", typeName);

            arrayNode.add(movieTypeJson);
        }

        return arrayNode;
    }

}
