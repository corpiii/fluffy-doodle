package com.clean_light.server.jwt.service;

import com.clean_light.server.jwt.dto.UserTokenInfo;
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
    public static final Duration ACCESS_EXPIRATION_TIME = Duration.ofMinutes(30);
    public static final Duration REFRESH_EXPIRATION_TIME = Duration.ofDays(7);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${secret_key}")
    private String SECRET_KEY;

    public String generateAccessToken(UserTokenInfo userTokenDTO) throws JsonProcessingException {
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
                    .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME.toMillis()))
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
                    .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME.toMillis()))
                    .and()
                .signWith(secretKey)
                .compact();
    }

    public UserTokenInfo decodeToken(String token) throws JsonProcessingException {
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            Claims payload = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String json = payload.get("user", String.class);

            return objectMapper.readValue(json, UserTokenInfo.class);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("토큰이 만료되었습니다.");
        } catch (JwtException e) {
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        }
    }
}
