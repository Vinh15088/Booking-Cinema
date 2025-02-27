package com.vinhSeo.BookingCinema.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketResponse implements Serializable {

    Integer id;
    Integer totalAmount;
    Date bookingDate;
    JsonNode showTime;
    List<TicketDetailResponse> ticketDetailResponses;
}
