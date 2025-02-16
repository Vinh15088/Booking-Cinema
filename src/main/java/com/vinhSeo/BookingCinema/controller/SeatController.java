package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.request.SeatRequest;
import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.dto.response.SeatResponse;
import com.vinhSeo.BookingCinema.mapper.SeatMapper;
import com.vinhSeo.BookingCinema.model.Seat;
import com.vinhSeo.BookingCinema.service.SeatService;
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
@Slf4j(topic = "SEAT_CONTROLLER")
@Tag(name = "Seat Controller")
@RequiredArgsConstructor
@Validated
@RequestMapping("/seat")
public class SeatController {

    private final SeatService seatService;
    private final SeatMapper seatMapper;

    @Operation(method = "POST", summary = "Create new seat",
            description = "Send a request via this API to create new seat")
    @PostMapping()
    public ResponseEntity<?> createSeat(@Valid @RequestBody SeatRequest seatRequest) {
        log.info("Create new seat");

        Seat seat = seatService.createNewSeat(seatRequest);

        SeatResponse seatResponse = seatMapper.toSeatResponse(seat);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Seat created successfully")
                .data(seatResponse)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get seat by Id",
            description = "Send a request via this API to get seat by Id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getSeatById(
            @PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Get seat by Id");;

        Seat seat = seatService.getSeatById(id);

        SeatResponse seatResponse = seatMapper.toSeatResponse(seat);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get seat by Id successfully")
                .data(seatResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get all seats in cinema hall",
            description = "Send a request via this API to get all seats in cinema hall")
    @GetMapping("/list")
    public ResponseEntity<?> getAllSeatsInCinemaHall(
            @RequestParam @Min(value = 1, message = "id must be greater than 0")  Integer cinemaHallId) {
        log.info("Get all seats in cinema hall");;

        List<Seat> seatList = seatService.getAllSeatsInCinemaHall(cinemaHallId);

        List<SeatResponse> seatResponseList = seatList.stream().map(seatMapper::toSeatResponse).toList();

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get all seats in cinema hall successfully")
                .data(seatResponseList)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "PUT", summary = "Update seat by Id",
            description = "Send a request via this API to update seat by Id")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSeat(
            @PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id,
            @Valid @RequestBody SeatRequest seatRequest) {
        log.info("Update seat by Id");;

        Seat seat = seatService.updateSeat(id, seatRequest);

        SeatResponse seatResponse = seatMapper.toSeatResponse(seat);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.ACCEPTED.value())
                .timestamp(new Date())
                .message("Update seat successfully")
                .data(seatResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "PUT", summary = "Change seat status",
            description = "Send a request via this API to change seat status")
    @PutMapping("/change-seat-status/{id}")
    public ResponseEntity<?> changeSeatStatus(
            @PathVariable @Min(value = 1, message = "id must be greater than 0") Integer id
            ) {
        log.info("Change seat status");

        Seat seat = seatService.changeSeatStatus(id);

        SeatResponse seatResponse = seatMapper.toSeatResponse(seat);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.ACCEPTED.value())
                .timestamp(new Date())
                .message("Change seat status successfully")
                .data(seatResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "DELETE", summary = "Delete seat by Id",
            description = "Send a request via this API to delete seat by Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSeat(@PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Delete seat by Id");;

        seatService.deleteSeat(id);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.RESET_CONTENT.value())
                .timestamp(new Date())
                .message("Delete seat successfully")
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }
}
