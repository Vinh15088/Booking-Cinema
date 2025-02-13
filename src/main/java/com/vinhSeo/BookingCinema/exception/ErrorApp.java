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

    USER_USERNAME_EXISTED("Username is existed",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_USER),
    USER_EMAIL_EXISTED("Email is existed",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_USER),
    USER_NOT_FOUND("User is not found",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_USER),
    USER_CHANGE_PASSWORD("Password is not equal to old password",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_USER),
    USER_PASSWORD_CONFIRM("Password is not equal to confirm password",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_USER),
    USER_OLD_PASSWORD("Old password is not correct",HttpStatus.BAD_REQUEST, ErrorCode.ERROR_USER),




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
