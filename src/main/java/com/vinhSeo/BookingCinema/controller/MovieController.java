package com.vinhSeo.BookingCinema.controller;


import com.vinhSeo.BookingCinema.dto.request.MovieRequest;
import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.dto.response.MovieResponse;
import com.vinhSeo.BookingCinema.mapper.MovieMapper;
import com.vinhSeo.BookingCinema.model.Movie;
import com.vinhSeo.BookingCinema.service.MovieService;
import com.vinhSeo.BookingCinema.utils.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/movie")
@Validated
@Tag(name = "Movie Controller")
@Slf4j(topic = "MOVIE_CONTROLLER")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final MovieMapper movieMapper;


    private static final String PAGE_SIZE = "10";
    private static final String PAGE_NUMBER = "1";

    @Operation(method = "POST", summary = "Create new movie",
            description = "Send a request via this API to create new movie")
    @PostMapping()
    public ResponseEntity<?> createMovie(@Valid @RequestBody MovieRequest movieRequest) {
        log.info("Create new movie");

        Movie movie = movieService.createMovie(movieRequest);

        MovieResponse movieResponse = movieMapper.toMovieResponse(movie);

        DataApiResponse dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Movie created successfully")
                .data(movieResponse)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get movie by Id",
            description = "Send a request via this API to get movie by Id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable @Min(value = 1, message = "id must be greater than 0")  int id) {
        log.info("Get movie by Id: {}", id);;
        Movie movie = movieService.getMovieById(id);

        MovieResponse movieResponse = movieMapper.toMovieResponse(movie);

        DataApiResponse dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get movie by Id successfully")
                .data(movieResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get movie by Title",
            description = "Send a request via this API to get movie by Title")
    @GetMapping()
    public ResponseEntity<?> getMovieByTitle(@RequestParam String title) {
        log.info("Get movie by Title: {}", title);;
        Movie movie = movieService.getMovieByTitle(title);

        MovieResponse movieResponse = movieMapper.toMovieResponse(movie);

        DataApiResponse dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get movie by Title successfully")
                .data(movieResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get movies with optional pagination and search",
            description = "Send a request via this API to get movies. You can optionally paginate and search.")
    @GetMapping("/list")
    public ResponseEntity<?> getMovies(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "number", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "order", defaultValue = "asc") String order
    ) {
        log.info("Get users with optional pagination and search");

        Page<Movie> moviePage = movieService.searchMovie(keyword, number-1, size, sortBy, order);

        List<Movie> movieList = moviePage.getContent();

        List<MovieResponse> userResponses = movieList.stream().map(movieMapper::toMovieResponse).toList();

        PageInfo pageInfo = null;
        if (moviePage != null) {
            pageInfo = PageInfo.builder()
                    .pageSize(moviePage.getSize())
                    .pageNumber(moviePage.getNumber() + 1)
                    .totalPages(moviePage.getTotalPages())
                    .totalElements(moviePage.getNumberOfElements())
                    .build();
        }

        DataApiResponse dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Search movies successfully")
                .data(userResponses)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "PUT", summary = "Update movie by Id",
            description = "Send a request via this API to update movie by Id")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable @Min(value = 1, message = "id must be greater than 0")  int id,
                                         @Valid @RequestBody MovieRequest movieRequest) {
        log.info("Update movie by Id: {}", id);;

        Movie movie = movieService.updateMovie(id, movieRequest);

        MovieResponse movieResponse = movieMapper.toMovieResponse(movie);

        DataApiResponse dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.ACCEPTED.value())
                .timestamp(new Date())
                .message("Update movie successfully")
                .data(movieResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "DELETE", summary = "Delete movie by Id",
            description = "Send a request via this API to delete movie by Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable @Min(value = 1, message = "id must be greater than 0")  int id) {
        log.info("Delete movie by Id: {}", id);;

        movieService.deleteMovie(id);

        DataApiResponse dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.RESET_CONTENT.value())
                .timestamp(new Date())
                .message("Delete movie successfully")
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }
    
}