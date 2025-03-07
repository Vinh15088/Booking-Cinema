package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.enums.TokenType;
import com.vinhSeo.BookingCinema.repository.BlackListTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j(topic = "JWT_SERVICE")
@RequiredArgsConstructor
public class JwtService {

    private final BlackListTokenRepository blackListTokenRepository;

    @Value("${jwt.expiryMinutes}")
    private int EXPIRY_MINUTES;

    @Value("${jwt.expiryDay}")
    private int EXPIRY_DAYS;

    @Value("${jwt.accessKey}")
    private String ACCESS_KEY;

    @Value("${jwt.refreshKey}")
    private String REFRESH_KEY;

    public String generateAccessToken(Integer userId, String username, Collection<? extends GrantedAuthority> authorities) throws Exception {
        log.info("Generate access token for user: {}, authorities {}", username, authorities);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);

        List<String> roles = new ArrayList<>();
        authorities.forEach(authority -> roles.add(authority.getAuthority()));
        claims.put("role", roles);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * EXPIRY_MINUTES))
                .signWith(getKey(TokenType.ACCESS_TOKEN), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(Integer userId, String username, Collection<? extends GrantedAuthority> authorities) throws Exception {
        log.info("Generate refresh token for user: {}, authorities {}", username, authorities);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);

        List<String> roles = new ArrayList<>();
        authorities.forEach(authority -> roles.add(authority.getAuthority()));
        claims.put("role", roles);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * EXPIRY_DAYS))
                .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsername(String token, TokenType tokenType) throws Exception {
        return extractClaim(token, tokenType, Claims::getSubject);
    }

    public Date extractExpiration(String token, TokenType tokenType) throws Exception {
        return extractClaim(token, tokenType, Claims::getExpiration);
    }

    public String extractId(String token, TokenType tokenType) throws Exception {
        return extractClaim(token, tokenType, Claims::getId);
    }

    private <T> T extractClaim(String token, TokenType tokenType, Function<Claims, T> claimsResolver) throws Exception {
        Claims claims = extractAllClaims(token, tokenType);

        String tokenId = claims.getId();

        if(blackListTokenRepository.existsById(tokenId)) {
            log.info("Blacklisted token: {}", tokenId);

            throw new AccessDeniedException("Access denied: Token " + tokenId + " is blacklisted");
        }

        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, TokenType tokenType) throws Exception {
        try {
            log.info("Extracting claims from token: {}", token);
            return Jwts.parser()
                    .verifyWith(getKey(tokenType))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (SignatureException | ExpiredJwtException e) {
            throw new AccessDeniedException("Access denied: Invalid token" + e.getMessage());
        }
    }

    private SecretKey getKey(TokenType tokenType) throws Exception {
        log.info("Generate key for token type: {}", tokenType);

        switch (tokenType) {
            case ACCESS_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(ACCESS_KEY));
            }
            case REFRESH_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(REFRESH_KEY));
            }
            default -> throw new Exception("Invalid token type");
        }
    }
}
