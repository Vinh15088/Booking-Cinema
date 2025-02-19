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
public class CinemaHallRequest implements Serializable {

    @NotBlank(message = "name must be not blank")
    String name;

    @NotNull(message = "hallRow must be not null")
    Integer hallRow;

    @NotNull(message = "hallColumn must be not null")
    Integer hallColumn;

    @NotNull(message = "available must be not null")
    Boolean available;

    @NotNull(message = "cinemaId must be not null")
    @Min(value = 1, message = "cinemaId is greater than 0")
    Integer cinemaId;

    @NotNull(message = "roomTypeId must be not null")
    @Min(value = 1, message = "roomTypeId is greater than 0")
    Integer roomTypeId;
}
