package com.vinhSeo.BookingCinema.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vinhSeo.BookingCinema.dto.request.CinemaHallRequest;
import com.vinhSeo.BookingCinema.dto.response.CinemaHallResponse;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.model.Cinema;
import com.vinhSeo.BookingCinema.model.CinemaHall;
import com.vinhSeo.BookingCinema.model.RoomType;
import com.vinhSeo.BookingCinema.repository.CinemaRepository;
import com.vinhSeo.BookingCinema.repository.RoomTypeRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CinemaHallMapper {

    @Mapping(target = "cinema", expression = "java(buildCinema(request, cinemaRepository))")
    @Mapping(target = "roomType", expression = "java(buildRoomType(request, roomTypeRepository))")
    CinemaHall toCinemaHall(CinemaHallRequest request,
                            @Context CinemaRepository cinemaRepository,
                            @Context RoomTypeRepository roomTypeRepository);

    @Mapping(target = "cinema", expression = "java(buildCinemaJson(cinemaHall))")
    @Mapping(target = "roomType", expression = "java(buildRoomTypeJson(cinemaHall))")
    CinemaHallResponse toCinemaResponse(CinemaHall cinemaHall);

    default Cinema buildCinema(CinemaHallRequest request, @Context CinemaRepository cinemaRepository) {
        return cinemaRepository.findById(request.getCinemaId()).orElseThrow(() ->
                new AppException(ErrorApp.CINEMA_NOT_FOUND));
    }

    default RoomType buildRoomType(CinemaHallRequest request, @Context RoomTypeRepository roomTypeRepository) {
        return roomTypeRepository.findById(request.getRoomTypeId()).orElseThrow(() ->
                new AppException(ErrorApp.ROOM_TYPE_NOT_FOUND));
    }

    default JsonNode buildCinemaJson(CinemaHall cinemaHall) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("cinemaId", cinemaHall.getCinema().getId());
        node.put("address", cinemaHall.getCinema().getAddress());
        node.put("city", cinemaHall.getCinema().getCity());

        return node;
    }

    default JsonNode buildRoomTypeJson(CinemaHall cinemaHall) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("typeRoomId", cinemaHall.getRoomType().getId());
        node.put("name", cinemaHall.getRoomType().getName());

        return node;
    }


}
