package com.vinhSeo.BookingCinema.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
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
public class CinemaHallResponse implements Serializable {

    Integer id;
    String name;
    Integer totalSeats;
    JsonNode cinema;
    JsonNode roomType;

}
