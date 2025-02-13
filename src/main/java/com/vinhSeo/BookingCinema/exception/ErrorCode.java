package com.vinhSeo.BookingCinema.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ERROR_LOGIN(1000, "Error login"),
    ERROR_MOVIE(1001, "Error movie"),
    ERROR_USER(1002, "Error user"),

    ;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
