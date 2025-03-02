package com.vinhSeo.BookingCinema.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewRequest implements Serializable {

    Integer rating;
    String comment;
    Integer movie;

}
