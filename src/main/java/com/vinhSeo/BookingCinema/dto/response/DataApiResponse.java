package com.vinhSeo.BookingCinema.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vinhSeo.BookingCinema.utils.PageInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor // For GET, POST
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataApiResponse<T> implements Serializable {
    boolean success;
    int code;
    String message;
    Date timestamp;
    T data;
    PageInfo pageInfo;

    // For PUT, PATCH, DELETE
    public DataApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
