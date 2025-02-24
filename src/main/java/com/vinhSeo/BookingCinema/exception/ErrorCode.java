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
    ERROR_SHOW_TIME(1008, "Error show time"),
    ERROR_SHOW_TIME_SEAT(1009, "Error show time seat"),
    ERROR_REVIEW(1010, "Error review"),
    ERROR_TICKET_PRICE(1011, "Error ticket price"),
    ERROR_TICKET(1012, "Error ticket"),


    ;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
