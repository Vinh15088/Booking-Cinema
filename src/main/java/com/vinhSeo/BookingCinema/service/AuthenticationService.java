package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.UserLoginRequest;
import com.vinhSeo.BookingCinema.dto.response.TokenResponse;
import com.vinhSeo.BookingCinema.enums.TokenType;
import com.vinhSeo.BookingCinema.enums.UserStatus;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.exception.InvalidDataException;
import com.vinhSeo.BookingCinema.exception.ResourceNotFoundException;
import com.vinhSeo.BookingCinema.model.BlackListToken;
import com.vinhSeo.BookingCinema.model.RedisMailConfirm;
import com.vinhSeo.BookingCinema.model.RedisToken;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.repository.BlackListTokenRepository;
import com.vinhSeo.BookingCinema.repository.RedisMailConfirmRepository;
import com.vinhSeo.BookingCinema.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpHeaders.REFERER;


@Service
@Slf4j(topic = "AUTHENTICATION_SERVICE")
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;
    private final RedisMailConfirmRepository redisMailConfirmRepository;
    private final BlackListTokenRepository blackListTokenRepository;

    public TokenResponse login(UserLoginRequest userLoginRequest) throws Exception {
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

        User user = userRepository.findByUsername(userLoginRequest.getUsername());

        String accessToken = jwtService.generateAccessToken(user.getId(), userLoginRequest.getUsername(), user.getAuthorities());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), userLoginRequest.getUsername(), user.getAuthorities());

        // save token with redis
        RedisToken redisToken = RedisToken.builder()
                .id(userLoginRequest.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        redisTokenService.save(redisToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logOut(String accessToken, String refreshToken, User user) throws Exception {
        log.info("Logout by user: {}", user.getId());

        String username = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);

        if(!user.getUsername().equals(username)) {
            throw new Exception("Invalid refresh token");
        }

        BlackListToken blackListAccessToken = BlackListToken.builder()
                .id(jwtService.extractId(accessToken, TokenType.ACCESS_TOKEN))
                .token(accessToken)
                .tokenType(TokenType.ACCESS_TOKEN)
                .expiredAt(jwtService.extractExpiration(accessToken, TokenType.ACCESS_TOKEN))
                .build();

        BlackListToken blackListRefreshToken = BlackListToken.builder()
                .id(jwtService.extractId(refreshToken, TokenType.REFRESH_TOKEN))
                .token(refreshToken)
                .tokenType(TokenType.REFRESH_TOKEN)
                .expiredAt(jwtService.extractExpiration(refreshToken, TokenType.REFRESH_TOKEN))
                .build();

        blackListTokenRepository.saveAll(List.of(blackListAccessToken, blackListRefreshToken));

        // clear authentication from securityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            SecurityContextHolder.clearContext();
            log.info("Logged out by user: {}", authentication.getPrincipal());
        }
    }

    public TokenResponse getRefreshToken(HttpServletRequest request) throws Exception {
        log.info("Get refresh token");

        String refreshToken = request.getHeader(REFERER);

        if(refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidDataException("Refresh token is empty");
        }

        try {
            String username = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);

            User user = userRepository.findByUsername(username);

            List<String> authorities = new ArrayList<>();
            user.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));

            String accessToken = jwtService.generateAccessToken(user.getId(), username, user.getAuthorities());

            // save token with redis
            RedisToken redisToken = RedisToken.builder()
                    .id(user.getUsername())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            redisTokenService.save(redisToken);

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            log.info("Refresh token failed: {}", e.getMessage());
            throw new InvalidDataException(e.getMessage());
        }
    }

    public String removeToken(HttpServletRequest request) throws Exception {
        log.info("Remove token");

        String token = request.getHeader(REFERER);
        if(StringUtils.isBlank(token)) {
            throw new InvalidDataException("Token must be not blank");
        }

        String username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);

        if(!redisTokenService.checkExisted(username)) {
            throw new InvalidDataException("Token is not found in Redis DB");
        }

        redisTokenService.remove(username);

        return "Remove token successfully";
    }

    public void confirmMail(String id, String email) {
        log.info("Confirm mail");

        RedisMailConfirm redisMailConfirm = redisMailConfirmRepository.findById(id).orElseThrow(() -> {
                log.info("RedisMailConfirm not found");
                return new ResourceNotFoundException("Redis mail confirm is not found");
            });

        if(redisMailConfirm.getEmail().equals(email)) {
            log.info("Confirm mail already exist");

            if(userRepository.findByEmail(email) == null) {
                throw new AppException(ErrorApp.USER_NOT_FOUND);
            }

            User user = userRepository.findByEmail(email);
            log.info("Verify user with email: {}, have full name: {}", email, user.getFullName());
            user.setUserStatus(UserStatus.ACTIVE);

            userRepository.save(user);

            log.info("Confirm mail successfully");

            redisMailConfirmRepository.delete(redisMailConfirm);
        } else {
            log.info("Confirm mail fail");
            throw new InvalidDataException("Confirm mail fail");
        }
    }
}
