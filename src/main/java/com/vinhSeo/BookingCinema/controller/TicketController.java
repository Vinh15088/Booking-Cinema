package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.request.TicketRequest;
import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.dto.response.TicketResponse;
import com.vinhSeo.BookingCinema.mapper.TicketDetailMapper;
import com.vinhSeo.BookingCinema.mapper.TicketMapper;
import com.vinhSeo.BookingCinema.model.RedisTicket;
import com.vinhSeo.BookingCinema.model.Ticket;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.service.BookingService;
import com.vinhSeo.BookingCinema.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/ticket")
@Validated
@Tag(name = "Ticket Controller")
@Slf4j(topic = "TICKET_CONTROLLER")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final TicketDetailMapper ticketDetailMapper;
    private final BookingService bookingService;

    @Operation(method = "POST", summary = "Booking ticket",
            description = "Send a request via this API to booking ticket by user")
    @PreAuthorize("hasAnyAuthority('USER')")
    @PostMapping("/booking")
    public ResponseEntity<?> bookingTicket(@Valid @RequestBody TicketRequest ticketRequest,
                                      @AuthenticationPrincipal User user) {
        log.info("Booking ticket request: {}", ticketRequest.toString());

        RedisTicket redisTicket = bookingService.bookingTicket(user.getId(), ticketRequest);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Hold seat successfully")
                .data(redisTicket)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "POST", summary = "Cancel hold seat",
            description = "Send a request via this API to cancel hold seat by user")
    @PreAuthorize("hasAnyAuthority('USER')")
    @PostMapping("/cancel-booking")
    public ResponseEntity<?> cancelBooking(@AuthenticationPrincipal User user) {
        log.info("Cancel booking ticket");

        bookingService.cancelBooking(user.getId());

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Cancel booking ticket successfully")
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get ticket by id",
            description = "Send a request via this API to get ticket by id")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'EMPLOYEE', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(
            @PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Get ticket by Id");;

        Ticket ticket = ticketService.getById(id);

        TicketResponse ticketResponse = ticketMapper.toTicketResponse(ticket, ticketDetailMapper);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get ticket by id successfully")
                .data(ticketResponse)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get all tickets or by userId, showTimeId",
            description = "Send a request via this API to all tickets or by userId, showTimeId")
    @GetMapping("/list")
    public ResponseEntity<?> getAllTickets(@AuthenticationPrincipal User user,
                                           @RequestParam(value = "showTimeId", required = false) Integer showTimeId) {
        log.info("Get all tickets or by userId, showTimeId");

        List<Ticket> ticketList = ticketService.getAllTickets(user.getId(), showTimeId);

        List<TicketResponse> ticketResponses = ticketList.stream().map(ticket ->
                ticketMapper.toTicketResponse(ticket, ticketDetailMapper)).toList();

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get all tickets or by userId, showTimeId successfully")
                .data(ticketResponses)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

}
