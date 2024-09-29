package com.clean_light.server.jwt.repository;

import com.clean_light.server.jwt.domain.TokenType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

public interface TokenRepository {
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    String fetchTokenBy(String loginId, TokenType type);

    void setToken(String key, String token, Duration duration, TokenType type);
    void deleteToken(String key, TokenType type);
}
