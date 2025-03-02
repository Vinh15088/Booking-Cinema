package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.request.ShowTimeRequest;
import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.dto.response.ShowTimeResponse;
import com.vinhSeo.BookingCinema.mapper.ShowTimeMapper;
import com.vinhSeo.BookingCinema.model.ShowTime;
import com.vinhSeo.BookingCinema.service.ShowTimeCacheService;
import com.vinhSeo.BookingCinema.service.ShowTimeService;
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
@Slf4j(topic = "SHOW_TIME_CONTROLLER")
@Tag(name = "Show Time Controller")
@RequiredArgsConstructor
@Validated
@RequestMapping("/show-time")
public class ShowTimeController {

    private final ShowTimeService showTimeService;
    private final ShowTimeMapper showTimeMapper;
    private final ShowTimeCacheService showTimeCacheService;

    @Operation(method = "POST", summary = "Create new show time",
            description = "Send a request via this API to create new show time")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @PostMapping()
    public ResponseEntity<?> createShowTime(@Valid @RequestBody ShowTimeRequest showTimeRequest) {
        log.info("Create new show time");

        ShowTime showTime = showTimeService.createShowTime(showTimeRequest);

        ShowTimeResponse showTimeResponse = showTimeMapper.toShowTimeResponse(showTime);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Show time created successfully")
                .data(showTimeResponse)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get show time by Id",
            description = "Send a request via this API to get show time by Id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getShowTimeById(
            @PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Get show time by Id");

        ShowTimeResponse showTimeResponse = showTimeCacheService.getCachedShowTime(id);

        if(showTimeResponse == null) {
            log.info("Show time response in redis cache is null");

            ShowTime showTime = showTimeService.getShowTimeById(id);

            showTimeResponse = showTimeMapper.toShowTimeResponse(showTime);

            showTimeCacheService.cacheShowTime(id, showTimeResponse);
        }


        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get show time by Id successfully")
                .data(showTimeResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get all show time with movie",
            description = "Send a request via this API to get all show time with movie")
    @GetMapping("/list-with-movie")
    public ResponseEntity<?> getAllShowTimeWithMovie(
            @RequestParam @Min(value = 1, message = "id must be greater than 0")  Integer movieId) {
        log.info("Get all show time with movie");

        List<ShowTimeResponse> showTimeResponseList = showTimeCacheService.getCachedShowTimesByMovie(movieId);

        if(showTimeResponseList == null) {
            log.info("Show time response in redis cache is empty");

            List<ShowTime> showTimeList = showTimeService.getShowTimesByMovie(movieId);

            showTimeResponseList = showTimeList.stream().map(showTimeMapper::toShowTimeResponse).toList();

            showTimeCacheService.cacheShowTimeByMovie(movieId, showTimeResponseList);
        }

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get all show time with movie successfully")
                .data(showTimeResponseList)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "PUT", summary = "Update show time by Id",
            description = "Send a request via this API to update show time by Id")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShowTime(
            @PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id,
            @Valid @RequestBody ShowTimeRequest showTimeRequest) {
        log.info("Update show time by Id");

        ShowTime showTime = showTimeService.updateShowTime(id, showTimeRequest);

        ShowTimeResponse showTimeResponse = showTimeMapper.toShowTimeResponse(showTime);

        showTimeCacheService.evictShowTimeCache(id);
        showTimeCacheService.cacheShowTime(id, showTimeResponse);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.ACCEPTED.value())
                .timestamp(new Date())
                .message("Update show time successfully")
                .data(showTimeResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "DELETE", summary = "Delete show time by Id",
            description = "Send a request via this API to delete show time by Id")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShowTime(@PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Delete show time by Id");;

        showTimeService.deleteShowTime(id);
        showTimeCacheService.evictShowTimeCache(id);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.RESET_CONTENT.value())
                .timestamp(new Date())
                .message("Delete show time successfully")
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }
}
