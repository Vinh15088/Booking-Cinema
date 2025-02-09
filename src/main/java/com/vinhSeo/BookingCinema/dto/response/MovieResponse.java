package com.vinhSeo.BookingCinema.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponse {
    String id;
    String title;
    String description;
    int duration;
    String language;
    int age_limit;
    Date release_date;
    Float rating;
    String status;
}
