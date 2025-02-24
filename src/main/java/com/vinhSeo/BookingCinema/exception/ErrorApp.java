package com.vinhSeo.BookingCinema.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorApp {
    LOGIN_USERNAME_ERROR("Username is not correct",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_LOGIN),
    LOGIN_PASSWORD_ERROR("Password is not correct",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_LOGIN),

    MOVIE_EXISTED("Movie is existed", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_MOVIE),
    MOVIE_NOT_FOUND("Movie is not found",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_MOVIE),
    MOVIE_ENDED("Movie is ended",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_MOVIE),
    MOVIE_TYPE_NOT_FOUND("Movie type is not found",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_MOVIE),

    USER_USERNAME_EXISTED("Username is existed",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_USER),
    USER_EMAIL_EXISTED("Email is existed",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_USER),
    USER_NOT_FOUND("User is not found",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_USER),
    USER_CHANGE_PASSWORD("Password is not equal to old password",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_USER),
    USER_PASSWORD_CONFIRM("Password is not equal to confirm password",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_USER),
    USER_OLD_PASSWORD("Old password is not correct",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_USER),

    CINEMA_NOT_FOUND("Cinema is not found", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_CINEMA),

    SEAT_TYPE_NOT_FOUND("Seat type is not found", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_SEAT_TYPE),

    SEAT_EXISTED_IN_CINEMA_HALL("Seat existed in this cinema hall", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_SEAT),
    SEAT_NOT_FOUND("Seat is not found", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_SEAT),

    CINEMA_HALL_NOT_FOUND("Cinema hall is not found", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_CINEMA_HALL),
    CINEMA_HALL_NOT_AVAILABLE("Cinema hall is not available", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_CINEMA_HALL),
    CINEMA_HALL_EXISTED_IN_CINEMA("Cinema hall is existed in cinema", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_CINEMA_HALL),

    ROOM_TYPE_NOT_FOUND("Room type is not found", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_ROOM_TYPE),

    SHOW_TIME_NOT_FOUND("Show time is not found", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_SHOW_TIME),
    SHOW_TIME_EXISTED("Show time is existed", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_SHOW_TIME),
    SHOW_TIME_CONFLICT("Show time is conflict", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_SHOW_TIME),

    SHOW_TIME_SEAT_NOT_FOUND("Show time seat is not found", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_SHOW_TIME_SEAT),
    SHOW_TIME_SEAT_STATUS_NOT_CORRECT("Show time seat status is not correct (AVAILABLE, PENDING, RESERVED)", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_SHOW_TIME_SEAT),

    REVIEW_EXISTED("Review is existed", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_REVIEW),
    REVIEW_NOT_FOUND("Review is not found", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_REVIEW),

    TICKET_PRICE_EXISTED("Ticket price is existed", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TICKET_PRICE),
    TICKET_PRICE_NOT_FOUND("Ticket price is not found", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TICKET_PRICE),

    TICKET_NOT_FOUND("Ticket is not found", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TICKET),
    TICKET_EXISTED("Ticket is existed", HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TICKET),

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
