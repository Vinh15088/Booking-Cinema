package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.request.TicketPriceRequest;
import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.dto.response.TicketPriceResponse;
import com.vinhSeo.BookingCinema.mapper.TicketPriceMapper;
import com.vinhSeo.BookingCinema.model.TicketPrice;
import com.vinhSeo.BookingCinema.repository.RoomTypeRepository;
import com.vinhSeo.BookingCinema.repository.SeatTypeRepository;
import com.vinhSeo.BookingCinema.service.TicketPriceService;
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
@RequestMapping("/ticket-price")
@Validated
@Tag(name = "Ticket Price Controller")
@Slf4j(topic = "TICKET_PRICE_CONTROLLER")
@RequiredArgsConstructor
public class TicketPriceController {

    private final TicketPriceService ticketPriceService;
    private final TicketPriceMapper ticketPriceMapper;
    private final RoomTypeRepository roomTypeRepository;
    private final SeatTypeRepository seatTypeRepository;

    private static final String PAGE_SIZE = "10";
    private static final String PAGE_NUMBER = "1";

    @Operation(method = "POST", summary = "Create new ticket price",
            description = "Send a request via this API to create new ticket price")
    @PostMapping()
    public ResponseEntity<?> createTicketPrice(@Valid @RequestBody TicketPriceRequest ticketPriceRequest) {
        log.info("Create new ticket price");

        TicketPrice ticketPrice = ticketPriceService.createTicketPrice(ticketPriceRequest);

        TicketPriceResponse ticketPriceResponse = ticketPriceMapper.toTicketPriceResponse(ticketPrice, roomTypeRepository, seatTypeRepository);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Ticket price created successfully")
                .data(ticketPriceResponse)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get ticket price by id",
            description = "Send a request via this API to get ticket price by id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketPriceById(
            @PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Get ticket price by Id");;

        TicketPrice ticketPrice = ticketPriceService.getById(id);

        TicketPriceResponse ticketPriceResponse = ticketPriceMapper.toTicketPriceResponse(ticketPrice, roomTypeRepository, seatTypeRepository);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get ticket price by id successfully")
                .data(ticketPriceResponse)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get all ticket price with optional pagination",
            description = "Send a request via this API to get all ticket price. You can optionally paginate.")
    @GetMapping("/list")
    public ResponseEntity<?> getAllTicketPrice(
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "number", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "order", defaultValue = "asc") String order
    ) {
        log.info("Get ticket price with optional pagination");

        Page<TicketPrice> ticketPricePage = ticketPriceService.getAll( size, number-1, sortBy, order);

        List<TicketPrice> ticketPriceList = ticketPricePage.getContent();

        List<TicketPriceResponse> ticketPriceResponses = ticketPriceList.stream().map(ticketPrice ->
                ticketPriceMapper.toTicketPriceResponse(ticketPrice, roomTypeRepository, seatTypeRepository)).toList();

        PageInfo pageInfo = null;
        if (ticketPricePage != null) {
            pageInfo = PageInfo.builder()
                    .pageSize(ticketPricePage.getSize())
                    .pageNumber(ticketPricePage.getNumber() + 1)
                    .totalPages(ticketPricePage.getTotalPages())
                    .totalElements(ticketPricePage.getNumberOfElements())
                    .build();
        }

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get all ticket price successfully")
                .data(ticketPriceResponses)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "PUT", summary = "Update price of ticket price by Id",
            description = "Send a request via this API to update price of ticket price by Id")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicketPrice(@PathVariable @Min(value = 1, message = "id must be greater than 0")  int id,
                                        @RequestParam Integer price) {
        log.info("Update price of ticket price by Id");

        TicketPrice ticketPrice = ticketPriceService.updateTicketPrice(id, price);

        TicketPriceResponse ticketPriceResponse = ticketPriceMapper.toTicketPriceResponse(ticketPrice, roomTypeRepository, seatTypeRepository);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.ACCEPTED.value())
                .timestamp(new Date())
                .message("Update price of ticket price successfully")
                .data(ticketPriceResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "DELETE", summary = "Delete ticket price by Id",
            description = "Send a request via this API to delete ticket price by Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicketPrice(@PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Delete ticket price by Id");

        ticketPriceService.deleteTicketPrice(id);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.RESET_CONTENT.value())
                .timestamp(new Date())
                .message("Delete ticket price successfully")
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

}
