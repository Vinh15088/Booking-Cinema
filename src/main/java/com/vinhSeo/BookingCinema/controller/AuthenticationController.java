package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.request.UserLoginRequest;
import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.dto.response.TokenResponse;
import com.vinhSeo.BookingCinema.model.RedisToken;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.service.AuthenticationService;
import com.vinhSeo.BookingCinema.service.RedisTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION_CONTROLLER")
@Tag(name = "Authentication Controller")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RedisTokenService redisTokenService;

    @PostMapping("/login")
    @Operation(method = "POST", summary = "Login and generate access & refresh tokens",
            description = "Send a request via this API to login by user")
    public ResponseEntity<?> getAccessToken(@RequestBody UserLoginRequest userLoginRequest) throws Exception {
        log.info("Get access token");

        TokenResponse tokenResponse = authenticationService.login(userLoginRequest);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Token created successfully")
                .data(tokenResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @PostMapping("/logout")
    @Operation(method = "POST", summary = "Logout by user",
            description = "Send a request via this API to logout account by user")
    public ResponseEntity<?> logout(@RequestParam String refreshToken,
                                    @AuthenticationPrincipal User user,
                                    @RequestHeader("Authorization") String header) throws Exception {
        String accessToken = header.substring(7);

        authenticationService.logOut(accessToken, refreshToken, user);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Logout successfully")
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @PostMapping("/refresh")
    @Operation(method = "POST", summary = "Refresh access token using refresh token",
            description = "Send a request via this API to refresh token")
    public ResponseEntity<?> getRefreshToken(HttpServletRequest request) throws Exception {
        log.info("Refresh token");

        TokenResponse tokenResponse = authenticationService.getRefreshToken(request);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Refresh token successfully")
                .data(tokenResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @PostMapping("/remove")
    @Operation(method = "POST", summary = "Remove token",
            description = "Send a request via this API to remove token")
    public ResponseEntity<?> removeToken(HttpServletRequest request) throws Exception {
        log.info("Remove token");

        String result = authenticationService.removeToken(request);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message(result)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    /* Test all token in redis */
    @GetMapping("/list")
    public ResponseEntity<?> getAllTokenInRedis() {
        log.info("Get all tokens in redis");

        List<RedisToken> redisTokens = redisTokenService.findAll();

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get all tokens in redis successfully")
                .data(redisTokens)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @GetMapping("/confirm-mail")
    @Operation(method = "GET", summary = "Confirm mail",
            description = "Send a request via this API to confirm mail")
    public ResponseEntity<?> confirmEmail(@RequestParam String id,
                                          @RequestParam String email) {
        log.info("Confirm mail");

        authenticationService.confirmMail(id, email);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Confirm mail successfully")
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }
}
