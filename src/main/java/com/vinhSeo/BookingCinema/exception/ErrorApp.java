package com.vinhSeo.BookingCinema.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorApp {
    MOVIE_EXISTED("Movie is existed", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_MOVIE),
    MOVIE_NOT_FOUND("Movie is not found",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_MOVIE),


    ;

    private String message;
    private HttpStatusCode httpStatusCode;
    private ErrorCode errorCode;

    ErrorApp(String message, HttpStatus httpStatusCode, ErrorCode errorCode) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
    }
}
