package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.model.ShowTimeSeat;
import com.vinhSeo.BookingCinema.service.ShowTimeSeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Slf4j(topic = "SHOW_TIME_SEAT_CONTROLLER")
@Tag(name = "Show Time Seat Controller")
@RequiredArgsConstructor
@Validated
@RequestMapping("/show-time-seat")
public class ShowTimeSeatController {
    private final ShowTimeSeatService showTimeSeatService;

    @Operation(method = "GET", summary = "Get show time seat by Id",
            description = "Send a request via this API to get show time seat by Id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getShowTimeById(
            @PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Get show time seat by Id");;

        ShowTimeSeat showTimeSeat = showTimeSeatService.getById(id);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get show time seat by Id successfully")
                .data(showTimeSeat)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get all show time seat by Show time",
            description = "Send a request via this API to get all show time seat by Show time")
    @GetMapping("/list")
    public ResponseEntity<?> getAllByShowTime(
            @RequestParam @Min(value = 1, message = "id must be greater than 0")  Integer showTimeId) {
        log.info("Get all show time seat by show time");;

        List<ShowTimeSeat> showTimeSeatList = showTimeSeatService.getAllByShowTime(showTimeId);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get all by show time successfully")
                .data(showTimeSeatList)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "PUT", summary = "Change status show time seat",
            description = "Send a request via this API to change status show time seat")
    @PreAuthorize("hasAnyAuthority('USER')")
    @PutMapping("/change-status")
    public ResponseEntity<?> changeStatus(
            @RequestParam @Min(value = 1, message = "id must be greater than 0")  Integer id,
            @RequestParam String status
            ) {
        log.info("Change status show time seat");

        ShowTimeSeat showTimeSeat = showTimeSeatService.changeStatus(id, status);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.ACCEPTED.value())
                .timestamp(new Date())
                .message("Change status show time seat successfully")
                .data(showTimeSeat)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }
}
