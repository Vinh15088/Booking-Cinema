package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.ReviewRequest;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.ReviewMapper;
import com.vinhSeo.BookingCinema.model.Movie;
import com.vinhSeo.BookingCinema.model.Review;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.repository.MovieRepository;
import com.vinhSeo.BookingCinema.repository.ReviewRepository;
import com.vinhSeo.BookingCinema.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "REVIEW_SERVICE")
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public Review createReview(Integer userId, ReviewRequest request) {
        log.info("Create review request by userId: {}", userId);

        Movie movie = movieRepository.findById(request.getMovie()).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new AppException(ErrorApp.USER_NOT_FOUND));

        if(reviewRepository.existsByUserAndMovie(user, movie)) {
            throw new AppException(ErrorApp.REVIEW_EXISTED);
        }

        Review review = reviewMapper.toReview(request, movieRepository);

        return reviewRepository.save(review);
    }

    public Review getReviewById(Integer id) {
        log.info("Get review by id: {}", id);

        return reviewRepository.findById(id).orElseThrow(() -> new AppException(ErrorApp.REVIEW_NOT_FOUND));
    }

    public Page<Review> getAllByUser(Integer userId, Integer number, Integer size, String sortBy, String order) {
        log.info("Get all reviews by userId: {}", userId);

        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sortBy);

        Pageable pageable = PageRequest.of(number, size, sort);

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorApp.USER_NOT_FOUND));

        return reviewRepository.findAllByUser(user, pageable);
    }

    public Page<Review> getAllByMovie(Integer movieId, Integer number, Integer size, String sortBy, String order) {
        log.info("Get all reviews by movieId: {}", movieId);

        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sortBy);

        Pageable pageable = PageRequest.of(number, size, sort);

        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new AppException(ErrorApp.MOVIE_NOT_FOUND));

        return reviewRepository.findAllByMovie(movie, pageable);
    }

    public Review updateReview(Integer id, Integer userId, ReviewRequest request) {
        log.info("Update review request by id: {}", id);

        Review review = getReviewById(id);

        if(!review.getUser().getId().equals(userId)) {
            throw new AppException(ErrorApp.REVIEW_ACCESS_DENIED);
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return reviewRepository.save(review);
    }

    public void deleteReview(Integer id, Integer userId) {
        log.info("Delete review by id: {}", id);

        Review review = getReviewById(id);

        if(!review.getUser().getId().equals(userId)) {
            throw new AppException(ErrorApp.REVIEW_ACCESS_DENIED);
        }

        reviewRepository.deleteById(id);
    }
}
