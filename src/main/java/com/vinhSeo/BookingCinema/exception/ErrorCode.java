package com.vinhSeo.BookingCinema.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ERROR_MOVIE(1001, "Error movie"),

    ;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
