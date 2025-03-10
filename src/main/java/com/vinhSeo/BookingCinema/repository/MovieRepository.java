package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    @Query("SELECT m FROM Movie m JOIN FETCH m.movieHasMovieTypes WHERE m.id = :id")
    Optional<Movie> findById(@Param("id") Integer id);

    Boolean existsByTitle(String title);
    Optional<Movie> findByTitle(String title);

    @Query("SELECT m FROM Movie m WHERE m.title LIKE %?1%")
    Page<Movie> findAll(String keyword, Pageable pageable);
}
