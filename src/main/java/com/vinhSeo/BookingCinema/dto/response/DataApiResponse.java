package com.vinhSeo.BookingCinema.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vinhSeo.BookingCinema.utils.PageInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor // For GET, POST
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataApiResponse<T> {
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
