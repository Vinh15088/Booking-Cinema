package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.request.UserLoginRequest;
import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.dto.response.TokenResponse;
import com.vinhSeo.BookingCinema.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION_CONTROLLER")
@Tag(name = "Authentication Controller")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping
    @Operation(method = "POST", summary = "Login and generate access & refresh tokens",
            description = "Send a request via this API to login by user")
    public ResponseEntity<?> getAccessToken(@RequestBody UserLoginRequest userLoginRequest) throws Exception {
        log.info("Get access token");

        TokenResponse tokenResponse = authenticationService.getAccessToken(userLoginRequest);

        DataApiResponse dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Token created successfully")
                .data(tokenResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @PostMapping("/refresh")
    @Operation(method = "POST", summary = "Refresh access token using refresh token",
            description = "Send a request via this API to refresh token")
    public ResponseEntity<?> getRefreshToken(@RequestParam String refreshToken) throws Exception {
        log.info("Refresh token");

        TokenResponse tokenResponse = authenticationService.getRefreshToken(refreshToken);

        DataApiResponse dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Refresh token successfully")
                .data(tokenResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }
}
