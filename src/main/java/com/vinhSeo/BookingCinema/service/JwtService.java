package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.Key;
import java.security.SignatureException;
import java.util.*;

@Service
@Slf4j(topic = "JWT_SERVICE")
public class JwtService {

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
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * EXPIRY_MINUTES))
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
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * EXPIRY_DAYS))
                .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsername(String token, TokenType tokenType) throws Exception {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getKey(tokenType))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (SignatureException | ExpiredJwtException e) {
            throw new AccessDeniedException("Access denied: Invalid token" + e.getMessage());
        }
    }

    private Key getKey(TokenType tokenType) throws Exception {
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
