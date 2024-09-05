package com.clean_light.server.jwt.service;

import com.clean_light.server.jwt.dto.UserTokenInfo;
import com.clean_light.server.user.dto.UserAuthToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:jwt.properties")
public class JwtService {
    public static final Duration ACCESS_EXPIRATION_TIME = Duration.ofMinutes(30);
    public static final Duration REFRESH_EXPIRATION_TIME = Duration.ofHours(6);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StringRedisTemplate jwtRedisTemplate;
    private final StringRedisTemplate blackListRedisTemplate;

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

    public UserAuthToken refresh(String accessToken, String refreshToken) throws JsonProcessingException {
        UserTokenInfo userTokenInfo = decodeToken(accessToken);
        String loginId = userTokenInfo.getLoginId();
        String storedRefreshToken = jwtRedisTemplate.opsForValue().get(loginId + "RT");

        if (storedRefreshToken == null || !Objects.equals(refreshToken, storedRefreshToken)) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }

        // 블랙리스트 처리
        sendToBlackListIfExist(loginId);

        String newAccessToken = generateAccessToken(userTokenInfo);
        String newRefreshToken = generateRefreshToken();

        jwtRedisTemplate.opsForValue().set(loginId + "AT", newAccessToken, ACCESS_EXPIRATION_TIME);
        jwtRedisTemplate.opsForValue().set(loginId + "RT", newRefreshToken, REFRESH_EXPIRATION_TIME);

        return UserAuthToken.of(newAccessToken, newRefreshToken);
    }

    public void sendToBlackListIfExist(String loginId) {
        String existedAccessToken = jwtRedisTemplate.opsForValue().get(loginId + "AT");
        String existedRefreshToken = jwtRedisTemplate.opsForValue().get(loginId + "RT");

        if (existedRefreshToken != null) {
            Duration refreshTokenExpiration = calculateTimeUntilExpiration(existedRefreshToken);

            blackListRedisTemplate.opsForValue().set(loginId + "RT", existedRefreshToken, refreshTokenExpiration);
            jwtRedisTemplate.delete(loginId + "RT");
        }

        if (existedAccessToken != null) {
            Duration accessTokenExpiration = calculateTimeUntilExpiration(existedAccessToken);

            blackListRedisTemplate.opsForValue().set(loginId + "AT", existedAccessToken, accessTokenExpiration);
            jwtRedisTemplate.delete(loginId + "AT");
        }
    }

    private Duration calculateTimeUntilExpiration(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        Claims payload = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        long difference = payload.getExpiration().getTime() - new Date().getTime();

        return Duration.ofMillis(difference);
    }

    public void setToken(String key, String accessToken, String refreshToken) {
        jwtRedisTemplate.opsForValue().set(key + "AT", accessToken, ACCESS_EXPIRATION_TIME);
        jwtRedisTemplate.opsForValue().set(key + "RT", refreshToken, REFRESH_EXPIRATION_TIME);
    }
}
