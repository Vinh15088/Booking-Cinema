package com.vinhSeo.BookingCinema.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketPriceRequest implements Serializable {

    @NotNull(message = "price must be not null")
    Integer price;

    @NotNull(message = "roomType must be not null")
    Integer roomType;

    @NotNull(message = "seatType must be not null")
    Integer seatType;
}
