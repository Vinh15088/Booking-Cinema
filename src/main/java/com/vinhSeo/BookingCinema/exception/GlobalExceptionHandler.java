package com.vinhSeo.BookingCinema.exception;

import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<DataApiResponse<?>> handlingAppException(AppException e) {
        ErrorApp errorApp = e.getErrorApp();

        DataApiResponse<?> apiResponse = DataApiResponse.builder()
                .success(false)
                .timestamp(new Date())
                .code(errorApp.getErrorCode().getCode())
                .message(errorApp.getMessage())
                .build();

        return ResponseEntity.status(errorApp.getHttpStatusCode()).body(apiResponse);
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<DataApiResponse<?>> handlingMethodArgumentNotValidException(
            MethodArgumentNotValidException exception){

        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();

        ErrorApp errorApp = ErrorApp.valueOf(enumKey);

        DataApiResponse<?> apiResponse =DataApiResponse.builder()
                .success(false)
                .timestamp(new Date())
                .code(errorApp.getErrorCode().getCode())
                .message(errorApp.getMessage())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    ResponseEntity<DataApiResponse<?>> handlingConstraintViolationException(
            ConstraintViolationException exception){
        String message = exception.getMessage();

        DataApiResponse<?> apiResponse =DataApiResponse.builder()
                .success(false)
                .timestamp(new Date())
                .code(BAD_REQUEST.value())
                .message(exception.getMessage().substring(message.indexOf(" ")+1))
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = NullPointerException.class)
    ResponseEntity<DataApiResponse<?>> handlingNullPointerException(
            NullPointerException exception){

        DataApiResponse<?> apiResponse =DataApiResponse.builder()
                .success(false)
                .timestamp(new Date())
                .code(INTERNAL_SERVER_ERROR.value())
                .message(exception.getMessage())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    ResponseEntity<DataApiResponse<?>> handlingNullPointerException(
            SQLIntegrityConstraintViolationException exception){

        DataApiResponse<?> apiResponse =DataApiResponse.builder()
                .success(false)
                .timestamp(new Date())
                .code(BAD_REQUEST.value())
                .message(exception.getMessage())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    ResponseEntity<DataApiResponse<?>> handlingIllegalArgumentException(
            IllegalArgumentException exception){

        DataApiResponse<?> apiResponse =DataApiResponse.builder()
                .success(false)
                .timestamp(new Date())
                .code(INTERNAL_SERVER_ERROR.value())
                .message(exception.getMessage())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }


}
