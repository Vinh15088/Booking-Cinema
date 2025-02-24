package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.TicketPriceRequest;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.TicketPriceMapper;
import com.vinhSeo.BookingCinema.model.RoomType;
import com.vinhSeo.BookingCinema.model.SeatType;
import com.vinhSeo.BookingCinema.model.TicketPrice;
import com.vinhSeo.BookingCinema.repository.RoomTypeRepository;
import com.vinhSeo.BookingCinema.repository.SeatTypeRepository;
import com.vinhSeo.BookingCinema.repository.TicketPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


@Service
@Slf4j(topic = "TICKET_PRICE_SERVICE")
@RequiredArgsConstructor
public class TicketPriceService {

    private final TicketPriceRepository ticketPriceRepository;
    private final TicketPriceMapper ticketPriceMapper;
    private final RoomTypeRepository roomTypeRepository;
    private final SeatTypeRepository seatTypeRepository;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public TicketPrice createTicketPrice(TicketPriceRequest request) {
        log.info("Create ticket price");

        RoomType roomType = roomTypeRepository.findById(request.getRoomType()).orElseThrow(() ->
                new AppException(ErrorApp.ROOM_TYPE_NOT_FOUND));

        SeatType seatType = seatTypeRepository.findById(request.getSeatType()).orElseThrow(() ->
                new AppException(ErrorApp.SEAT_TYPE_NOT_FOUND));

        if(ticketPriceRepository.existsTicketPriceByRoomTypeAndSeatType(roomType.getId(), seatType.getId())) {
            throw new AppException(ErrorApp.TICKET_PRICE_EXISTED);
        }

        TicketPrice ticketPrice = ticketPriceMapper.toTicketPrice(request);

        return ticketPriceRepository.save(ticketPrice);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public TicketPrice getById(Integer id) {
        log.info("Get ticket price by id: {}", id);

        return ticketPriceRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorApp.TICKET_PRICE_NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<TicketPrice> getAll(Integer size, Integer number, String sortBy, String order) {
        log.info("Get ticket price all");

        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sortBy);

        Pageable pageable = PageRequest.of(number, size, sort);

        return ticketPriceRepository.findAll(pageable);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public TicketPrice updateTicketPrice(Integer id, Integer price) {
        log.info("Update ticket price by id: {}", id);

        TicketPrice ticketPrice = getById(id);
        ticketPrice.setPrice(price);

        return ticketPriceRepository.save(ticketPrice);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void deleteTicketPrice(Integer id) {
        log.info("Delete ticket price by id: {}", id);

        TicketPrice ticketPrice = getById(id);

        ticketPriceRepository.delete(ticketPrice);
    }


}
