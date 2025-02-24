package com.vinhSeo.BookingCinema.mapper;

import com.vinhSeo.BookingCinema.dto.request.TicketDetailRequest;
import com.vinhSeo.BookingCinema.dto.response.TicketDetailResponse;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.model.RoomType;
import com.vinhSeo.BookingCinema.model.SeatType;
import com.vinhSeo.BookingCinema.model.ShowTimeSeat;
import com.vinhSeo.BookingCinema.model.TicketDetail;
import com.vinhSeo.BookingCinema.repository.ShowTimeSeatRepository;
import com.vinhSeo.BookingCinema.repository.TicketPriceRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketDetailMapper {

    @Mapping(target = "price", expression = "java(buildPrice(request, showTimeSeatRepository, ticketPriceRepository))")
    TicketDetail toTicketDetail(TicketDetailRequest request,
                                @Context ShowTimeSeatRepository showTimeSeatRepository,
                                @Context TicketPriceRepository ticketPriceRepository);

    TicketDetailResponse toTicketDetailResponse(TicketDetail ticketDetail);

    default Integer buildPrice(TicketDetailRequest request,
                               ShowTimeSeatRepository showTimeSeatRepository,
                               TicketPriceRepository ticketPriceRepository) {
        ShowTimeSeat showTimeSeat = showTimeSeatRepository.findById(request.getShowTimeSeatId()).orElseThrow(() ->
                new AppException(ErrorApp.SHOW_TIME_NOT_FOUND));

        SeatType seatType = showTimeSeat.getSeat().getSeatType();
        RoomType roomType = showTimeSeat.getShowTime().getCinemaHall().getRoomType();

        return ticketPriceRepository.findTicketPriceByRoomTypeAndSeatType(roomType.getId(), seatType.getId()).getPrice();
    }

}
