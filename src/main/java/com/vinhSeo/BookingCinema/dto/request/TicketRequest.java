package com.vinhSeo.BookingCinema.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketRequest implements Serializable {

    @NotNull(message = "showTime (Id) must be not null")
    Integer showTime;

    @NotEmpty(message = "ticketDetailRequests must be not null")
    List<TicketDetailRequest> ticketDetailRequests;
}
