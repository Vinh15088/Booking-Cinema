package com.vinhSeo.BookingCinema.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vinhSeo.BookingCinema.dto.request.TicketDetailRequest;
import com.vinhSeo.BookingCinema.dto.request.TicketRequest;
import com.vinhSeo.BookingCinema.dto.response.TicketDetailResponse;
import com.vinhSeo.BookingCinema.dto.response.TicketResponse;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.model.ShowTime;
import com.vinhSeo.BookingCinema.model.Ticket;
import com.vinhSeo.BookingCinema.model.TicketDetail;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.repository.ShowTimeRepository;
import com.vinhSeo.BookingCinema.repository.ShowTimeSeatRepository;
import com.vinhSeo.BookingCinema.repository.TicketPriceRepository;
import com.vinhSeo.BookingCinema.repository.UserRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "user", expression = "java(buildUser(request, userRepository))")
    @Mapping(target = "showTime", expression = "java(buildShowTime(request, showTimeRepository))")
    @Mapping(target = "ticketDetails", expression = "java(buildTicketDetail(request, " +
            "ticketDetailMapper, showTimeSeatRepository, ticketPriceRepository))")
    @Mapping(target = "totalAmount", expression = "java(buildTotalAmount(request))")
    Ticket toTicket(TicketRequest request,
                    @Context UserRepository userRepository,
                    @Context ShowTimeRepository showTimeRepository,
                    @Context TicketDetailMapper ticketDetailMapper,
                    @Context ShowTimeSeatRepository showTimeSeatRepository,
                    @Context TicketPriceRepository ticketPriceRepository);

    @Mapping(target = "user", expression = "java(buildUserJson(ticket))")
    @Mapping(target = "showTime", expression = "java(buildShowTimeJson(ticket))")
    @Mapping(target = "ticketDetailResponses", expression = "java(buildTicketDetailResponse(ticket, ticketDetailMapper))")
    TicketResponse toTicketResponse(Ticket ticket, @Context TicketDetailMapper ticketDetailMapper);

    default User buildUser(TicketRequest request, @Context UserRepository userRepository) {
        return userRepository.findById(request.getUser()).orElseThrow(() ->
                new AppException(ErrorApp.USER_NOT_FOUND));
    }

    default ShowTime buildShowTime(TicketRequest request, @Context ShowTimeRepository showTimeRepository) {
        return showTimeRepository.findById(request.getShowTime()).orElseThrow(() ->
                new AppException(ErrorApp.SHOW_TIME_NOT_FOUND));
    }

    default List<TicketDetail> buildTicketDetail(TicketRequest request,
                                                 @Context TicketDetailMapper ticketDetailMapper,
                                                 @Context ShowTimeSeatRepository showTimeSeatRepository,
                                                 @Context TicketPriceRepository ticketPriceRepository) {

        List<TicketDetailRequest> requestList = request.getTicketDetailRequests();

        List<TicketDetail> ticketDetails = requestList.stream()
                .map(detailRequest -> ticketDetailMapper.toTicketDetail(detailRequest, showTimeSeatRepository, ticketPriceRepository))
                .toList();

        return ticketDetails;
    }

    default Integer buildTotalAmount(TicketRequest request) {
        return request.getTicketDetailRequests().size();
    }

    default JsonNode buildUserJson(Ticket ticket) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("userId", ticket.getUser().getId());
        node.put("username", ticket.getUser().getUsername());

        return node;
    }

    default JsonNode buildShowTimeJson(Ticket ticket) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("showTimeId", ticket.getShowTime().getId());
        node.put("cinemaHallId", ticket.getShowTime().getCinemaHall().getId());
        node.put("movieId", ticket.getShowTime().getMovie().getId());
        node.put("startTime", ticket.getShowTime().getStartTime().getTime());
        node.put("endTime", ticket.getShowTime().getEndTime().getTime());

        return node;
    }

    default List<TicketDetailResponse> buildTicketDetailResponse(Ticket ticket, @Context TicketDetailMapper ticketDetailMapper) {
        List<TicketDetail> ticketDetails = ticket.getTicketDetails();

        return ticketDetails.stream().map(ticketDetailMapper::toTicketDetailResponse).toList();
    }
}
