package com.vinhSeo.BookingCinema.dto.request;

import com.vinhSeo.BookingCinema.dto.validator.EnumValue;
import com.vinhSeo.BookingCinema.enums.CinemaStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CinemaRequest implements Serializable {

    @NotBlank(message = "address must be not blank")
    String address;

    @NotBlank(message = "city must be not blank")
    String city;

    @NotNull(message = "latitude must be not null")
    BigDecimal latitude;

    @NotNull(message = "longitude must be not null")
    BigDecimal longitude;

    @EnumValue(name = "cinemaStatus", enumClass = CinemaStatus.class)
    String cinemaStatus;
}
