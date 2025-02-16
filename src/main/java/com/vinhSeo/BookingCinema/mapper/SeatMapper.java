package com.vinhSeo.BookingCinema.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vinhSeo.BookingCinema.dto.request.SeatRequest;
import com.vinhSeo.BookingCinema.dto.response.SeatResponse;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.model.CinemaHall;
import com.vinhSeo.BookingCinema.model.Seat;
import com.vinhSeo.BookingCinema.model.SeatType;
import com.vinhSeo.BookingCinema.repository.CinemaHallRepository;
import com.vinhSeo.BookingCinema.repository.SeatTypeRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(target = "seatType", expression = "java(buildSeatType(request, seatTypeRepository))")
    @Mapping(target = "cinemaHall", expression = "java(buildSeatCinemaHall(request, cinemaHallRepository))")
    Seat toSeat(SeatRequest request,
                @Context SeatTypeRepository seatTypeRepository,
                @Context CinemaHallRepository cinemaHallRepository);

    @Mapping(target = "seatType", expression = "java(buildSeatTypeJson(seat))")
    @Mapping(target = "cinemaHall", expression = "java(buildCinemaHallJson(seat))")
    SeatResponse toSeatResponse(Seat seat);

    default SeatType buildSeatType(SeatRequest request, @Context SeatTypeRepository seatTypeRepository) {
        return seatTypeRepository.findById(request.getSeatTypeId()).orElseThrow(() ->
                new AppException(ErrorApp.SEAT_TYPE_NOT_FOUND));
    }

    default CinemaHall buildSeatCinemaHall(SeatRequest request, @Context CinemaHallRepository cinemaHallRepository) {
        return cinemaHallRepository.findById(request.getCinemaHallId()).orElseThrow(() ->
                new AppException(ErrorApp.CINEMA_HALL_NOT_FOUND));
    }

    default JsonNode buildSeatTypeJson(Seat seat) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("seatTypeId", seat.getSeatType().getId());
        node.put("seatTypeName", seat.getSeatType().getName());

        return node;
    }

    default JsonNode buildCinemaHallJson(Seat seat) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("cinemaHallId", seat.getCinemaHall().getId());
        node.put("cinemaHallName", seat.getCinemaHall().getName());

        return node;
    }
}
