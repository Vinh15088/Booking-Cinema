package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.request.CinemaRequest;
import com.vinhSeo.BookingCinema.dto.response.CinemaResponse;
import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.mapper.CinemaMapper;
import com.vinhSeo.BookingCinema.model.Cinema;
import com.vinhSeo.BookingCinema.service.CinemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j(topic = "CINEMA_CONTROLLER")
@Tag(name = "Cinema Controller")
@RequiredArgsConstructor
@Validated
@RequestMapping("/cinema")
public class CinemaController {

    private final CinemaService cinemaService;
    private final CinemaMapper cinemaMapper;

    @Operation(method = "POST", summary = "Create new cinema",
            description = "Send a request via this API to create new cinema")
    @PostMapping()
    public ResponseEntity<?> createCinema(@Valid @RequestBody CinemaRequest cinemaRequest) {
        log.info("Create new cinema");

        Cinema cinema = cinemaService.createNewCinema(cinemaRequest);

        CinemaResponse cinemaResponse = cinemaMapper.toCinemaResponse(cinema);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Cinema created successfully")
                .data(cinemaResponse)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get cinema by Id",
            description = "Send a request via this API to get cinema by Id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCinemaById(
            @PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Get cinema by Id");;

        Cinema cinema = cinemaService.getCinemaById(id);

        CinemaResponse cinemaResponse = cinemaMapper.toCinemaResponse(cinema);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get cinema by Id successfully")
                .data(cinemaResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get all cinemas",
            description = "Send a request via this API to get all cinemas")
    @GetMapping("/list")
    public ResponseEntity<?> getAllCinemas() {
        log.info("Get all cinemas");

        List<Cinema> cinemaList = cinemaService.getAllCinemas();

        List<CinemaResponse> cinemaResponseList = cinemaList.stream().map(cinemaMapper::toCinemaResponse).toList();

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get all cinemas successfully")
                .data(cinemaResponseList)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "PUT", summary = "Update cinema by Id",
            description = "Send a request via this API to update cinema by Id")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCinema(
            @PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id,
            @Valid @RequestBody CinemaRequest cinemaRequest) {
        log.info("Update cinema by Id");;

        Cinema cinema = cinemaService.updateCinema(id, cinemaRequest);

        CinemaResponse cinemaResponse = cinemaMapper.toCinemaResponse(cinema);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.ACCEPTED.value())
                .timestamp(new Date())
                .message("Update cinema successfully")
                .data(cinemaResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "DELETE", summary = "Delete cinema by Id",
            description = "Send a request via this API to delete cinema by Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCinema(@PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Delete cinema by Id");;

        cinemaService.deleteCinema(id);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.RESET_CONTENT.value())
                .timestamp(new Date())
                .message("Delete cinema successfully")
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }
}
