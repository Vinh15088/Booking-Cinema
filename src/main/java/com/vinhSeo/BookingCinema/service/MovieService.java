package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.MovieRequest;
import com.vinhSeo.BookingCinema.enums.MovieStatus;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.MovieMapper;
import com.vinhSeo.BookingCinema.model.Movie;
import com.vinhSeo.BookingCinema.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public Movie createMovie(MovieRequest request) {
        if(movieRepository.existsByTitle(request.getTitle())) throw  new AppException(ErrorApp.MOVIE_EXISTED);

        Movie movie = movieMapper.toMovie(request);

        if(request.getStatus() == null || request.getStatus().isEmpty()) {
            movie.setStatus(MovieStatus.SHOWING);
        }

        return movieRepository.save(movie);
    }

    public Movie getMovieById(int id) {
        Movie movie = movieRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));

        return movie;
    }

    public Movie getMovieByTitle(String title) {
        Movie movie = movieRepository.findByTitle(title).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));

        return movie;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Page<Movie> getPageMovies(Integer number, Integer size, String sortBy, String order) {
        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sortBy);

        Pageable pageable = PageRequest.of(number, size, sort);

        return movieRepository.findAll(pageable);
    }

    public Page<Movie> searchMoviesWithTitle(String title, Integer number, Integer size, String sortBy, String order) {
        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sortBy);

        Pageable pageable = PageRequest.of(number, size, sort);

        return (title != null && !title.isEmpty())
                ? movieRepository.findByTitleContaining(title, pageable)
                : movieRepository.findAll(pageable);
    }

    public Movie updateMovie(int id, MovieRequest request) {
        Movie movie = getMovieById(id);

        Movie newMovie = movieMapper.toMovie(request);
        newMovie.setId(id);

        return movieRepository.save(newMovie);
    }

    public void deleteMovie(int id) {
        getMovieById(id);

        movieRepository.deleteById(id);
    }
}
