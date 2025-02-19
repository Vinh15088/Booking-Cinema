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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@Service
@Slf4j(topic = "MOVIE_SERVICE")
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final CloudinaryService cloudinaryService;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Movie createMovie(MovieRequest request, MultipartFile trailer, MultipartFile banner) throws IOException {
        log.info("Create new movie");

        if(movieRepository.existsByTitle(request.getTitle())) throw  new AppException(ErrorApp.MOVIE_EXISTED);

        Movie movie = movieMapper.toMovie(request);
        movie.setTrailer(trailer.getOriginalFilename());
        movie.setBanner(banner.getOriginalFilename());

        if(request.getStatus() == null || request.getStatus().isEmpty()) {
            movie.setStatus(MovieStatus.SHOWING);
        }

        movieRepository.save(movie);

        String folderTrailer = "BookingCinema/Trailer/" + movie.getId();
        String folderBanner = "BookingCinema/Banner/" + movie.getId();

        log.info("folderTrailer: {}", folderTrailer);
        log.info("folderBanner: {}", folderBanner);

        cloudinaryService.uploadFile(banner, folderBanner);
        cloudinaryService.uploadVideo(trailer, folderTrailer);

        return movie;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public Movie getMovieById(int id) {
        log.info("Get movie by id: {}", id);

        Movie movie = movieRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));

        return movie;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
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

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Movie updateMovie(int id, MovieRequest request, MultipartFile trailer, MultipartFile banner) throws IOException {
        log.info("Update movie with id: {}", id);

        Movie movie = getMovieById(id);

        Movie newMovie = movieMapper.toMovie(request);
        newMovie.setId(id);

        String folderTrailer = "BookingCinema/Trailer/" + movie.getId();
        String folderBanner = "BookingCinema/Banner/" + movie.getId();

        String publicIdTrailer = folderTrailer + "/" + movie.getTrailer().substring(0, movie.getTrailer().lastIndexOf("."));
        String publicIdBanner = folderBanner + "/" + movie.getBanner().substring(0, movie.getBanner().lastIndexOf("."));

        Map<String, String> updateFile = cloudinaryService.updateFile(banner, folderBanner, publicIdBanner);
        Map<String, String> updateTrailer = cloudinaryService.updateVideo(trailer, folderTrailer, publicIdTrailer);

        if(updateFile != null) newMovie.setBanner(banner.getOriginalFilename());
        if(updateTrailer != null) newMovie.setTrailer(trailer.getOriginalFilename());

        return movieRepository.save(newMovie);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void deleteMovie(int id) throws IOException {
        log.info("Delete movie with id: {}", id);

        Movie movie = getMovieById(id);

        String publicIdTrailer = "BookingCinema/Trailer/" + movie.getId();
        String publicIdBanner = "BookingCinema/Banner/" + movie.getId() ;

        movieRepository.deleteById(id);

        cloudinaryService.deleteVideo(publicIdTrailer);
        cloudinaryService.deleteFile(publicIdBanner);

        log.info("Delete banner, trailer of movie id {} successfully", id);
    }
}
