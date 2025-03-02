package com.vinhSeo.BookingCinema.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vinhSeo.BookingCinema.dto.request.ReviewRequest;
import com.vinhSeo.BookingCinema.dto.response.ReviewResponse;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.model.Movie;
import com.vinhSeo.BookingCinema.model.Review;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.repository.MovieRepository;
import com.vinhSeo.BookingCinema.repository.UserRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "movie", expression = "java(buildMovie(request, movieRepository))")
    Review toReview(ReviewRequest request, @Context MovieRepository movieRepository);

    @Mapping(target = "user", expression = "java(buildUserJson(review))")
    @Mapping(target = "movie", expression = "java(buildMovieJson(review))")
    ReviewResponse toReviewResponse(Review review);

    default Movie buildMovie(ReviewRequest request, @Context MovieRepository movieRepository) {
        return movieRepository.findById(request.getMovie()).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));
    }

    default JsonNode buildUserJson(Review review) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("userId", review.getUser().getId());
        node.put("username", review.getUser().getUsername());

        return node;
    }

    default JsonNode buildMovieJson(Review review) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("movieId", review.getMovie().getId());
        node.put("title", review.getMovie().getTitle());

        return node;
    }
}
