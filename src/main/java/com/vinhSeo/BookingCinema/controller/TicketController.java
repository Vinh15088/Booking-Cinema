package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.request.TicketRequest;
import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.dto.response.TicketResponse;
import com.vinhSeo.BookingCinema.mapper.TicketDetailMapper;
import com.vinhSeo.BookingCinema.mapper.TicketMapper;
import com.vinhSeo.BookingCinema.model.Ticket;
import com.vinhSeo.BookingCinema.service.TicketService;
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
@RequestMapping("/ticket")
@Validated
@Tag(name = "Ticket Controller")
@Slf4j(topic = "TICKET_CONTROLLER")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final TicketDetailMapper ticketDetailMapper;

    @Operation(method = "POST", summary = "Create new ticket",
            description = "Send a request via this API to create new ticket")
    @PostMapping()
    public ResponseEntity<?> createTicket(@Valid @RequestBody TicketRequest ticketRequest) {
        log.info("Create new ticket");

        Ticket ticket = ticketService.createTicket(ticketRequest);

        TicketResponse ticketResponse = ticketMapper.toTicketResponse(ticket, ticketDetailMapper);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Ticket created successfully")
                .data(ticketResponse)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get ticket by id",
            description = "Send a request via this API to get ticket by id")
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
    public ResponseEntity<?> getAllTickets(@RequestParam(value = "userId", required = false) Integer userId,
                                           @RequestParam(value = "showTimeId", required = false) Integer showTimeId) {
        log.info("Get all tickets or by userId, showTimeId");

        List<Ticket> ticketList = ticketService.getAllTickets(userId, showTimeId);

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

    @Operation(method = "PUT", summary = "Update ticket by Id",
            description = "Send a request via this API to update ticket by Id")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable @Min(value = 1, message = "id must be greater than 0")  int id,
                                          @Valid @RequestBody TicketRequest ticketRequest) {
        log.info("Update ticket by Id");;

        Ticket ticket = ticketService.updateTicket(id, ticketRequest);

        TicketResponse ticketResponse = ticketMapper.toTicketResponse(ticket, ticketDetailMapper);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.ACCEPTED.value())
                .timestamp(new Date())
                .message("Update ticket successfully")
                .data(ticketResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "DELETE", summary = "Delete ticket by Id",
            description = "Send a request via this API to delete ticket by Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Delete ticket by Id");

        ticketService.deleteTicket(id);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.RESET_CONTENT.value())
                .timestamp(new Date())
                .message("Delete ticket successfully")
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }
}
