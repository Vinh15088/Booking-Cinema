package com.vinhSeo.BookingCinema.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class SeatRequest implements Serializable {

    @NotBlank(message = "seatNumber must be not blank")
    String seatNumber;

    @NotNull(message = "seatStatus must be not null")
    Boolean seatStatus;

    @NotNull(message = "seatTypeId must be not null")
    @Min(value = 1, message = "seatTypeId is greater than 0")
    Integer seatTypeId;

    @NotNull(message = "cinemaHallId must be not null")
    @Min(value = 1, message = "cinemaHallId is greater than 0")
    Integer cinemaHallId;

}
