package com.vinhSeo.BookingCinema.dto.request;

import com.vinhSeo.BookingCinema.dto.validator.EnumValue;
import com.vinhSeo.BookingCinema.enums.CinemaStatus;
import jakarta.validation.constraints.NotBlank;
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
public class CinemaRequest implements Serializable {

    @NotBlank(message = "address must be not blank")
    String address;

    @NotBlank(message = "city must be not blank")
    String city;

    @EnumValue(name = "cinemaStatus", enumClass = CinemaStatus.class)
    String cinemaStatus;
}
