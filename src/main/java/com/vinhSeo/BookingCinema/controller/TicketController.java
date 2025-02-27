package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.request.TicketRequest;
import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.dto.response.TicketResponse;
import com.vinhSeo.BookingCinema.mapper.TicketDetailMapper;
import com.vinhSeo.BookingCinema.mapper.TicketMapper;
import com.vinhSeo.BookingCinema.model.RedisTicket;
import com.vinhSeo.BookingCinema.model.Ticket;
import com.vinhSeo.BookingCinema.service.TicketService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.accessKey}")
    private String ACCESS_KEY;


    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final TicketDetailMapper ticketDetailMapper;

    @Operation(method = "POST", summary = "Create new ticket",
            description = "Send a request via this API to create new ticket")
    @PostMapping()
    public ResponseEntity<?> createTicket(@Valid @RequestBody TicketRequest ticketRequest,
                                          @RequestHeader("Authorization") String token
                                          ) {
        log.info("Create new ticket");

        String jwtToken = token.replace("Bearer ", "");

        Integer userId;

        Claims claims = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(ACCESS_KEY)))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
        userId = claims.get("id", Integer.class);


        Ticket ticket = ticketService.createTicket(userId, ticketRequest);

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

    @Operation(method = "POST", summary = "Hold seat",
            description = "Send a request via this API to hold seat by user")
    @PostMapping("/hold-seat")
    public ResponseEntity<?> holdSeat(@Valid @RequestBody TicketRequest ticketRequest,
                                          @RequestHeader("Authorization") String token
    ) {
        log.info("Hold seat");

        String jwtToken = token.replace("Bearer ", "");

        Integer userId;

        Claims claims = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(ACCESS_KEY)))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
        userId = claims.get("id", Integer.class);


        RedisTicket redisTicket = ticketService.holdSeat(userId, ticketRequest);


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
    @PostMapping("/cancel-hold-seat")
    public ResponseEntity<?> cancelHoldSeat(@RequestHeader("Authorization") String token
    ) {
        log.info("Cancel hold seat");

        String jwtToken = token.replace("Bearer ", "");

        Integer userId;

        Claims claims = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(ACCESS_KEY)))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
        userId = claims.get("id", Integer.class);


        ticketService.cancelHoldSeat(userId);


        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Cancel hold seat successfully")
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


}
