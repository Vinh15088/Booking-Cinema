package com.vinhSeo.BookingCinema.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ERROR_LOGIN(1000, "Error login"),
    ERROR_MOVIE(1001, "Error movie"),
    ERROR_USER(1002, "Error user"),
    ERROR_CINEMA(1003, "Error cinema"),
    ERROR_SEAT_TYPE(1004, "Error seat type"),
    ERROR_SEAT(1005, "Error seat"),
    ERROR_CINEMA_HALL(1006, "Error cinema hall"),
    ERROR_ROOM_TYPE(1007, "Error room type"),

    ;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
