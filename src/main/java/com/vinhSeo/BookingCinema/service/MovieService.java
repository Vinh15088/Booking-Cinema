package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.MovieRequest;
import com.vinhSeo.BookingCinema.enums.MovieStatus;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.MovieMapper;
import com.vinhSeo.BookingCinema.model.Movie;
import com.vinhSeo.BookingCinema.model.MovieHasMovieType;
import com.vinhSeo.BookingCinema.model.MovieType;
import com.vinhSeo.BookingCinema.repository.MovieHasMovieTypeRepository;
import com.vinhSeo.BookingCinema.repository.MovieRepository;
import com.vinhSeo.BookingCinema.repository.MovieTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j(topic = "MOVIE_SERVICE")
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final CloudinaryService cloudinaryService;
    private final MovieTypeRepository movieTypeRepository;
    private final MovieHasMovieTypeRepository movieHasMovieTypeRepository;

    @Transactional(rollbackFor = Exception.class)
    public Movie createMovie(MovieRequest request, MultipartFile banner) {
        log.info("Create new movie");

        if(movieRepository.existsByTitle(request.getTitle())) throw  new AppException(ErrorApp.MOVIE_EXISTED);

        Movie movie = movieMapper.toMovie(request, movieTypeRepository);
        movie.setBanner(banner.getOriginalFilename());

        if(request.getStatus() == null || request.getStatus().isEmpty()) {
            movie.setStatus(MovieStatus.SHOWING);
        }

        movieRepository.save(movie);

        // store movie has movie type to db
        List<MovieHasMovieType> movieHasMovieTypes = movie.getMovieHasMovieTypes();

        for (MovieHasMovieType movieHasMovieType : movieHasMovieTypes) {
            movieHasMovieType.setMovie(movie);
        }

        movieHasMovieTypeRepository.saveAll(movieHasMovieTypes);

        // push banner image to cloudinary
        String folderBanner = "BookingCinema/Banner/" + movie.getId();

        log.info("folderBanner: {}", folderBanner);

        CompletableFuture<Void> bannerUpload = cloudinaryService.uploadFileAsync(banner, folderBanner)
                .thenAccept(result -> log.info("Banner uploaded successfully", result.get("secure_url")));

        CompletableFuture.allOf(bannerUpload);

        return movie;
    }

    @Transactional
    @Cacheable(value = "movie", key = "#id")
    public Movie getMovieById(Integer id) {
        log.info("Get movie by id: {}", id);

        return movieRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));
    }

    public Movie getMovieByTitle(String title) {
        log.info("Get movie by title: {}", title);

        return movieRepository.findByTitle(title).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_NOT_FOUND));
    }

    public List<Movie> getMovieByMovieType(Integer movieTypeId) {
        log.info("Get movie by movie type: {}", movieTypeId);

        MovieType movieType = movieTypeRepository.findById(movieTypeId).orElseThrow(() ->
                new AppException(ErrorApp.MOVIE_TYPE_NOT_FOUND));

        List<MovieHasMovieType> movieHasMovieTypes = movieHasMovieTypeRepository.findAllByMovieType(movieType);

        List<Movie> movies = movieHasMovieTypes.stream().map(MovieHasMovieType::getMovie).toList();

        return movies;
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

    @Transactional(rollbackFor = Exception.class)
    @CachePut(value = "movie", key = "#id")
    public Movie updateMovie(Integer id, MovieRequest request, MultipartFile banner) throws IOException {
        log.info("Update movie with id: {}", id);

        Movie movie = getMovieById(id);
        Movie newMovie = movieMapper.toMovie(request, movieTypeRepository);
        newMovie.setId(id);

        String folderBanner = "BookingCinema/Banner/" + movie.getId();
        String publicIdBanner = folderBanner + "/" + movie.getBanner().substring(0, movie.getBanner().lastIndexOf("."));

        if(banner != null && !banner.getOriginalFilename().isEmpty()) {
            newMovie.setBanner(banner.getOriginalFilename());

            CompletableFuture<Void> updateFile = cloudinaryService.uploadFileAsync(banner, folderBanner)
                    .thenAccept(result -> log.info("Update new banner successfully", result.get("secure_url")));

            CompletableFuture<Void> deleteFile = cloudinaryService.deleteFile(publicIdBanner)
                    .thenAccept(result -> log.info("Delete old banner successfully", result.get("secure_url")));

            CompletableFuture.allOf(updateFile, deleteFile);
        }

        return movieRepository.save(newMovie);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMovie(int id) throws IOException {
        log.info("Delete movie with id: {}", id);

        Movie movie = getMovieById(id);

        String publicIdBanner = "BookingCinema/Banner/" + movie.getId() ;

        movieRepository.deleteById(id);

        CompletableFuture<Void> deleteFile = cloudinaryService.deleteFile(publicIdBanner)
                .thenAccept(result -> log.info("Delete banner successfully", result.get("secure_url")));

        CompletableFuture.allOf(deleteFile);

        log.info("Delete banner, trailer of movie id {} successfully", id);
    }
}
