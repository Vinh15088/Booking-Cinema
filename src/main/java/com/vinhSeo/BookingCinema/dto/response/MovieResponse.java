package com.vinhSeo.BookingCinema.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponse implements Serializable {
    String id;
    String title;
    String description;
    int duration;
    String language;
    int ageLimit;
    Date releaseDate;
    Float rating;
    String status;
}
