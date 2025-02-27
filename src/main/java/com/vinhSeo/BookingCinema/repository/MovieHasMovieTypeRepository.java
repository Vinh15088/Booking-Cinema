package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.MovieHasMovieType;
import com.vinhSeo.BookingCinema.model.MovieType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieHasMovieTypeRepository extends JpaRepository<MovieHasMovieType, Integer> {

    List<MovieHasMovieType> findAllByMovieType(MovieType movieType);

}
