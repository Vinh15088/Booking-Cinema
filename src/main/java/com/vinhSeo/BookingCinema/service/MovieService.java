package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.MovieRequest;
import com.vinhSeo.BookingCinema.enums.MovieStatus;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.MovieMapper;
import com.vinhSeo.BookingCinema.model.Movie;
import com.vinhSeo.BookingCinema.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Slf4j(topic = "MOVIE_SERVICE")
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public Movie createMovie(MovieRequest request) {
        log.info("Create new movie");

        if(movieRepository.existsByTitle(request.getTitle())) throw  new AppException(ErrorApp.MOVIE_EXISTED);

        Movie movie = movieMapper.toMovie(request);

        if(request.getStatus() == null || request.getStatus().isEmpty()) {
            movie.setStatus(MovieStatus.SHOWING);
        }

        return movieRepository.save(movie);
    }

    public Movie getMovieById(int id) {
        log.info("Get movie by id: {}", id);

        Movie movie = movieRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));

        return movie;
    }

    public Movie getMovieByTitle(String title) {
        log.info("Get movie by title: {}", title);

        Movie movie = movieRepository.findByTitle(title).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));

        return movie;
    }

    public Page<Movie> searchMovie(String keyword, Integer number, Integer size, String sortBy, String order) {
        log.info("Search movie with keyword: {}", keyword);

        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sortBy);

        Pageable pageable = PageRequest.of(number, size, sort);

        Page<Movie> result = (keyword != null && !keyword.isEmpty())
                ? movieRepository.findAll(keyword, pageable)
                : movieRepository.findAll(pageable);

        log.info("Search result: {}", result.getContent());
        return result;
    }

    public Movie updateMovie(int id, MovieRequest request) {
        log.info("Update movie with id: {}", id);

        Movie movie = getMovieById(id);

        Movie newMovie = movieMapper.toMovie(request);
        newMovie.setId(id);

        return movieRepository.save(newMovie);
    }

    public void deleteMovie(int id) {
        log.info("Delete movie with id: {}", id);

        getMovieById(id);

        movieRepository.deleteById(id);
    }
}
