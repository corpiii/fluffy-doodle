package com.clean_light.server.jwt.repository;

import com.clean_light.server.jwt.domain.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

import static com.clean_light.server.jwt.domain.TokenType.ACCESS;

@Repository
@Profile("default")
@RequiredArgsConstructor
public class BlackListRedisRepository implements TokenRepository {
    private final StringRedisTemplate blackListRedisTemplate;

    private String generateSuffix(TokenType type) {
        return type == ACCESS ? "AT" : "RT";
    }

    @Override
    public String fetchTokenBy(String loginId, TokenType type) {
        String suffix = generateSuffix(type);

        return blackListRedisTemplate.opsForValue().get(loginId + suffix);
    }

    @Override
    public void setToken(String key, String token, Duration duration, TokenType type) {
        String suffix = generateSuffix(type);

        blackListRedisTemplate.opsForValue().set(key + suffix, token, duration);
    }

    @Override
    public void deleteToken(String key, TokenType type) {
        String suffix = generateSuffix(type);

        blackListRedisTemplate.delete(key + suffix);
    }
}
