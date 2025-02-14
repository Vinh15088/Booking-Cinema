package com.vinhSeo.BookingCinema.exception;

import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AppException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request - Custom application exception",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Login Username Error",
                                    summary = "Username is not correct",
                                    value = """
                                        {
                                            "success": false,
                                            "timestamp": "2025-02-13T11:12:53.457+00:00",
                                            "code": 400,
                                            "message": "Username is not correct"
                                        }
                                        """
                            ))})
    })
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

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Handle exception when the data invalid. (@RequestBody, @RequestParam, @PathVariable)",
                                    summary = "Handle Bad Request",
                                    value = """
                                            {
                                                 "success": false,
                                                 "timestamp": "2025-02-13T11:12:53.457+00:00",
                                                 "code": 400,
                                                 "message": "{data} must be not blank"
                                             }
                                            """
                            ))})
    })
    @ExceptionHandler(value = {MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class, ConstraintViolationException.class})
    ResponseEntity<DataApiResponse<?>> handlingValidationException(Exception e){

        String message = e.getMessage();
        String errorMessage = "";
        if (e instanceof MethodArgumentNotValidException) {
            int start = message.lastIndexOf("[") + 1;
            int end = message.lastIndexOf("]") - 1;
            message = message.substring(start, end);
            errorMessage = "Invalid Payload: " + message;
        } else if (e instanceof MissingServletRequestParameterException) {
            errorMessage = "Invalid Parameter: " + message;
        } else if (e instanceof ConstraintViolationException) {
            message = message.substring(message.indexOf(" ") + 1);
            errorMessage = "Invalid Parameter: " + message;
        } else {
            errorMessage = "Invalid Data: " + message;
        }

        DataApiResponse<?> apiResponse =DataApiResponse.builder()
                .success(false)
                .timestamp(new Date())
                .code(BAD_REQUEST.value())
                .message(errorMessage)
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = NullPointerException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Handle exception when null pointer occurs.",
                                    summary = "Handle Null Pointer Exception",
                                    value = """
                                        {
                                             "success": false,
                                             "timestamp": "2025-02-13T11:12:53.457+00:00",
                                             "code": 500,
                                             "message": "Null pointer exception occurred"
                                         }
                                        """
                            ))})
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Handle exception when SQL integrity constraint violation occurs.",
                                    summary = "Handle SQL Integrity Constraint Violation",
                                    value = """
                                        {
                                             "success": false,
                                             "timestamp": "2025-02-13T11:12:53.457+00:00",
                                             "code": 400,
                                             "message": "Duplicate entry: The data you are trying to insert already exists."
                                         }
                                        """
                            ))})
    })
    ResponseEntity<DataApiResponse<?>> handlingNullPointerException(
            SQLIntegrityConstraintViolationException exception){

        String errorMessage = exception.getMessage();
        String customMessage = null;

        if (errorMessage != null) {
            if (errorMessage.contains("Duplicate entry")) {
                customMessage = "Duplicate entry: The data you are trying to insert already exists.";
            } else if (errorMessage.contains("foreign key constraint")) {
                customMessage = "Invalid reference: The referenced data does not exist.";
            } else if (errorMessage.contains("cannot be null")) {
                customMessage = "Missing required field: Some required fields are missing or null.";
            } else {
                customMessage = "SQL Integrity Constraint violation.";
            }
        }

        DataApiResponse<?> apiResponse = DataApiResponse.builder()
                .success(false)
                .timestamp(new Date())
                .code(BAD_REQUEST.value())
                .message(customMessage)
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Forbidden - Access Denied",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Access Denied Error",
                                    summary = "Handle Access Denied Exception",
                                    value = """
                                        {
                                             "success": false,
                                             "timestamp": "2025-02-13T11:12:53.457+00:00",
                                             "code": 403,
                                             "message": "You do not have permission to access this resource."
                                         }
                                        """
                            ))})
    })
    ResponseEntity<DataApiResponse<?>> handlingForBiddenException(
            AccessDeniedException exception){

        DataApiResponse<?> apiResponse =DataApiResponse.builder()
                .success(false)
                .timestamp(new Date())
                .code(FORBIDDEN.value())
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
