package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.UserLoginRequest;
import com.vinhSeo.BookingCinema.dto.response.TokenResponse;
import com.vinhSeo.BookingCinema.enums.TokenType;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j(topic = "AUTHENTICATION_SERVICE")
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public TokenResponse getAccessToken(UserLoginRequest userLoginRequest) throws Exception {
        log.info("Get access token");

        List<String> authorities = new ArrayList<>();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword()));

            log.info("Authenticated: {}", authentication.isAuthenticated());
            log.info("Authorities: {}", authentication.getAuthorities().toString());

            authorities.add(authentication.getAuthorities().toString());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.info("Authentication failed: {}", e.getMessage());
            if (userRepository.findByUsername(userLoginRequest.getUsername()) == null) {
                throw new AppException(ErrorApp.LOGIN_USERNAME_ERROR);
            } else {
                throw new AppException(ErrorApp.LOGIN_PASSWORD_ERROR);
            }
        }

        String accessToken = jwtService.generateAccessToken(userLoginRequest.getUsername(), authorities);
        String refreshToken = jwtService.generateRefreshToken(userLoginRequest.getUsername(), authorities);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenResponse getRefreshToken(String refreshToken) throws Exception {
        log.info("Get refresh token");

        if(refreshToken == null || refreshToken.isEmpty()) {
            throw new Exception("Refresh token is empty");
        }

        try {
            String username = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);

            User user = userRepository.findByUsername(username);

            List<String> authorities = new ArrayList<>();
            user.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));

            String accessToken = jwtService.generateAccessToken(username, authorities);

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            log.info("Refresh token failed: {}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
