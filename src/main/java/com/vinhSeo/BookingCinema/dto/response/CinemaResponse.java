package com.vinhSeo.BookingCinema.dto.response;

import com.vinhSeo.BookingCinema.enums.CinemaStatus;
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
public class CinemaResponse implements Serializable {

    Integer id;
    String address;
    String city;
    CinemaStatus cinemaStatus;

}
