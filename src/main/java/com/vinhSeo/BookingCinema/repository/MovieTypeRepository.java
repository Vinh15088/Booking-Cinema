package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.MovieType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieTypeRepository extends JpaRepository<MovieType, Integer> {
}
