package com.vinhSeo.BookingCinema.utils;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageInfo {
    Integer pageSize;
    Integer pageNumber;
    Integer totalElements;
    Integer totalPages;

    boolean hasNext;
    boolean hasPrevious;
}
