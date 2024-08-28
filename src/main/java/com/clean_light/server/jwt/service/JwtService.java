package com.clean_light.server.jwt.service;

import com.clean_light.server.jwt.dto.UserTokenDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:jwt.properties")
public class JwtService {
    private static final long ACCESS_EXPIRATION_TIME = Duration.ofMinutes(30).toMillis();
    private static final long REFRESH_EXPIRATION_TIME = Duration.ofDays(7).toMillis();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${secret_key}")
    private String SECRET_KEY;

    public String generateAccessToken(UserTokenDTO userTokenDTO) throws JsonProcessingException {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        String json = objectMapper.writeValueAsString(userTokenDTO);

        return Jwts.builder()
                .header()
                    .add("type", "jwt")
                    .and()
                .claims()
                    .add("user", json)
                    .add("tokenType", "access")
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))
                .and()
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken() throws JsonProcessingException {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .header()
                    .add("type", "jwt")
                    .and()
                .claims()
                    .add("tokenType", "refresh")
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                    .and()
                .signWith(secretKey)
                .compact();
    }

    public UserTokenDTO decodeToken(String token) throws JsonProcessingException {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        Claims payload = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String json = payload.get("user", String.class);

        return objectMapper.readValue(json, UserTokenDTO.class);
    }
}
