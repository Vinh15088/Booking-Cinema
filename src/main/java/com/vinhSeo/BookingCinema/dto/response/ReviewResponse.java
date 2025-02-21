package com.vinhSeo.BookingCinema.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse implements Serializable {

    Integer id;
    Integer rating;
    String comment;
    JsonNode user;
    JsonNode movie;
}
