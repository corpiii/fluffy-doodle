package com.clean_light.server.jwt.repository;

import com.clean_light.server.jwt.domain.TokenType;

import java.time.Duration;

public interface TokenRepository {
    String fetchTokenBy(String loginId, TokenType type);
    void setToken(String key, String token, Duration duration, TokenType type);
    void deleteToken(String key, TokenType type);
}
