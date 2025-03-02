package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.request.CinemaHallRequest;
import com.vinhSeo.BookingCinema.dto.response.CinemaHallResponse;
import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.mapper.CinemaHallMapper;
import com.vinhSeo.BookingCinema.model.CinemaHall;
import com.vinhSeo.BookingCinema.service.CinemaHallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j(topic = "CINEMA_HALL_CONTROLLER")
@Tag(name = "Cinema Hall Controller")
@RequiredArgsConstructor
@Validated
@RequestMapping("/cinema-hall")
public class CinemaHallController {

    private final CinemaHallService cinemaHallService;
    private final CinemaHallMapper cinemaHallMapper;

    @Operation(method = "POST", summary = "Create new cinema hall",
            description = "Send a request via this API to create new cinema hall")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping()
    public ResponseEntity<?> createCinemaHall(@Valid @RequestBody CinemaHallRequest cinemaHallRequest) {
        log.info("Create new cinema hall");

        CinemaHall cinemaHall = cinemaHallService.createCinemaHall(cinemaHallRequest);

        CinemaHallResponse cinemaHallResponse = cinemaHallMapper.toCinemaResponse(cinemaHall);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Cinema hall created successfully")
                .data(cinemaHallResponse)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get cinema hall by Id",
            description = "Send a request via this API to get cinema hall by Id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCinemaHallById(
            @PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Get cinema hall by Id");;

        CinemaHall cinemaHall = cinemaHallService.getCinemaHallById(id);

        CinemaHallResponse cinemaHallResponse = cinemaHallMapper.toCinemaResponse(cinemaHall);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get cinema hall by Id successfully")
                .data(cinemaHallResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get all cinema hall with keyword",
            description = "Send a request via this API to get all cinema hall with keyword")
    @GetMapping("/list")
    public ResponseEntity<?> getAllCinemaHallWithKeyword(@RequestParam String keyword) {
        log.info("Get all cinema hall with keyword");;

        List<CinemaHall> cinemaHallList = cinemaHallService.getAllCinemaHall(keyword);

        List<CinemaHallResponse> cinemaHallResponseList = cinemaHallList.stream().map(cinemaHallMapper::toCinemaResponse).toList();

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get all cinema hall with keyword successfully")
                .data(cinemaHallResponseList)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "PUT", summary = "Update cinema hall by Id",
            description = "Send a request via this API to update cinema hall by Id")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCinemaHall(
            @PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id,
            @Valid @RequestBody CinemaHallRequest cinemaHallRequest) {
        log.info("Update cinema hall by Id");;

        CinemaHall cinemaHall = cinemaHallService.updateCinemaHall(id, cinemaHallRequest);

        CinemaHallResponse cinemaHallResponse = cinemaHallMapper.toCinemaResponse(cinemaHall);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.ACCEPTED.value())
                .timestamp(new Date())
                .message("Update cinema hall successfully")
                .data(cinemaHallResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "DELETE", summary = "Delete cinema hall by Id",
            description = "Send a request via this API to delete cinema hall by Id")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSeat(@PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Delete cinema hall by Id");;

        cinemaHallService.deleteCinemaHall(id);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.RESET_CONTENT.value())
                .timestamp(new Date())
                .message("Delete cinema hall successfully")
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }
}
