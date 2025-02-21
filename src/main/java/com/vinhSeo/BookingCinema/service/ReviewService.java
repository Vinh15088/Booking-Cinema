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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "REVIEW_SERVICE")
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    @PreAuthorize("hasAnyAuthority('USER')")
    public Review createReview(ReviewRequest request) {
        log.info("Create review request by userId: {}", request.getUser());

        Movie movie = movieRepository.findById(request.getMovie()).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));

        User user = userRepository.findById(request.getUser()).orElseThrow(() ->
                new AppException(ErrorApp.USER_NOT_FOUND));

        if(reviewRepository.existsByUserAndMovie(user, movie)) {
            throw new AppException(ErrorApp.REVIEW_EXISTED);
        }

        Review review = reviewMapper.toReview(request, userRepository, movieRepository);

        log.info("id: {}", review.getId());
        log.info("rating: {}", review.getRating());
        log.info("comment: {}", review.getComment());
        log.info("idUser: {}", review.getUser().getId());
        log.info("idMovie: {}", review.getMovie().getId());

        return reviewRepository.save(review);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public Review getReviewById(Integer id) {
        log.info("Get review by id: {}", id);

        return reviewRepository.findById(id).orElseThrow(() -> new AppException(ErrorApp.REVIEW_NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public Page<Review> getAllByUser(Integer userId, Integer number, Integer size, String sortBy, String order) {
        log.info("Get all reviews by userId: {}", userId);

        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sortBy);

        Pageable pageable = PageRequest.of(number, size, sort);

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorApp.USER_NOT_FOUND));

        return reviewRepository.findAllByUser(user, pageable);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public Page<Review> getAllByMovie(Integer movieId, Integer number, Integer size, String sortBy, String order) {
        log.info("Get all reviews by movieId: {}", movieId);

        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sortBy);

        Pageable pageable = PageRequest.of(number, size, sort);

        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new AppException(ErrorApp.MOVIE_NOT_FOUND));

        return reviewRepository.findAllByMovie(movie, pageable);
    }

    @PreAuthorize("hasAnyAuthority('USER')")
    public Review updateReview(Integer id, ReviewRequest request) {
        log.info("Update review request by userId: {}", request.getUser());

        Review review = getReviewById(id);
        ReviewRequest newRequest = ReviewRequest.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(review.getUser().getId())
                .movie(review.getMovie().getId())
                .build();

        Review newReview = reviewMapper.toReview(newRequest, userRepository, movieRepository);
        newReview.setId(id);

        return reviewRepository.save(newReview);
    }

    @PreAuthorize("hasAnyAuthority('USER')")
    public void deleteReview(Integer id) {
        log.info("Delete review by id: {}", id);

        getReviewById(id);

        reviewRepository.deleteById(id);
    }
}
