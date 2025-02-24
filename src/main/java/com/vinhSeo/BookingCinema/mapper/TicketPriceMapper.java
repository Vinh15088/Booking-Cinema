package com.vinhSeo.BookingCinema.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vinhSeo.BookingCinema.dto.request.TicketPriceRequest;
import com.vinhSeo.BookingCinema.dto.response.TicketPriceResponse;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.model.RoomType;
import com.vinhSeo.BookingCinema.model.SeatType;
import com.vinhSeo.BookingCinema.model.TicketPrice;
import com.vinhSeo.BookingCinema.repository.RoomTypeRepository;
import com.vinhSeo.BookingCinema.repository.SeatTypeRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketPriceMapper {

    TicketPrice toTicketPrice(TicketPriceRequest request);

    @Mapping(target = "roomType", expression = "java(buildRoomTypeJson(ticketPrice, roomTypeRepository))")
    @Mapping(target = "seatType", expression = "java(buildSeatTypeJson(ticketPrice, seatTypeRepository))")
    TicketPriceResponse toTicketPriceResponse(TicketPrice ticketPrice,
                                              @Context RoomTypeRepository roomTypeRepository,
                                              @Context SeatTypeRepository seatTypeRepository);

    default JsonNode buildRoomTypeJson(TicketPrice ticketPrice, RoomTypeRepository roomTypeRepository) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        RoomType roomType = roomTypeRepository.findById(ticketPrice.getRoomType()).orElseThrow(() ->
                new AppException(ErrorApp.ROOM_TYPE_NOT_FOUND));

        node.put("roomTypeId", roomType.getId());
        node.put("roomTypeName", roomType.getName());

        return node;
    }

    default JsonNode buildSeatTypeJson(TicketPrice ticketPrice, SeatTypeRepository seatTypeRepository) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        SeatType seatType = seatTypeRepository.findById(ticketPrice.getSeatType()).orElseThrow(() ->
                new AppException(ErrorApp.SEAT_TYPE_NOT_FOUND));

        node.put("seatTypeId", seatType.getId());
        node.put("seatTypeName", seatType.getName());

        return node;
    }

}
