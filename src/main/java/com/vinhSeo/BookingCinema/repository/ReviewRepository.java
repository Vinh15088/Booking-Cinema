package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.Movie;
import com.vinhSeo.BookingCinema.model.Review;
import com.vinhSeo.BookingCinema.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Page<Review> findAllByUser(User user, Pageable pageable);

    Page<Review> findAllByMovie(Movie movie, Pageable pageable);

    Boolean existsByUserAndMovie(User user, Movie movie);
}
