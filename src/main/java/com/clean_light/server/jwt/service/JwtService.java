package com.clean_light.server.jwt.service;

import com.clean_light.server.jwt.dto.UserTokenInfo;
import com.clean_light.server.jwt.repository.TokenRepository;
import com.clean_light.server.user.dto.UserAuthToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;

import static com.clean_light.server.jwt.domain.TokenType.*;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:jwt.properties")
public class JwtService {
    public static final Duration ACCESS_EXPIRATION_TIME = Duration.ofMinutes(30);
    public static final Duration REFRESH_EXPIRATION_TIME = Duration.ofHours(6);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TokenRepository redisRepository;
    private final TokenRepository blackListRedisRepository;

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

    public String generateRefreshToken() {
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
        String storedRefreshToken = redisRepository.fetchTokenBy(loginId, REFRESH);

        if (storedRefreshToken == null || !Objects.equals(refreshToken, storedRefreshToken)) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }

        sendToBlackListIfExist(loginId);

        String newAccessToken = generateAccessToken(userTokenInfo);
        String newRefreshToken = generateRefreshToken();

        setToken(loginId, newAccessToken, newRefreshToken);

        return UserAuthToken.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void sendToBlackListIfExist(String loginId) {
        String existedRefreshToken = redisRepository.fetchTokenBy(loginId, REFRESH);
        String existedAccessToken = redisRepository.fetchTokenBy(loginId, ACCESS);

        if (existedRefreshToken != null) {
            Duration refreshTokenExpiration = calculateTimeUntilExpiration(existedRefreshToken);

            blackListRedisRepository.setToken(loginId, existedRefreshToken, refreshTokenExpiration, REFRESH);
            redisRepository.deleteToken(loginId, REFRESH);
        }

        if (existedAccessToken != null) {
            Duration accessTokenExpiration = calculateTimeUntilExpiration(existedAccessToken);

            blackListRedisRepository.setToken(loginId, existedAccessToken, accessTokenExpiration, ACCESS);
            redisRepository.deleteToken(loginId, ACCESS);
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
        redisRepository.setToken(key, accessToken, ACCESS_EXPIRATION_TIME, ACCESS);
        redisRepository.setToken(key, refreshToken, REFRESH_EXPIRATION_TIME, REFRESH);
    }
}
