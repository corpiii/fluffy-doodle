package com.clean_light.server.jwt.repository;

import com.clean_light.server.jwt.domain.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import static com.clean_light.server.jwt.domain.TokenType.*;

@Repository
@Profile("default")
@RequiredArgsConstructor
public class RedisRepository implements TokenRepository {
    private final StringRedisTemplate jwtRedisTemplate;

    private String generateSuffix(TokenType type) {
        return type == ACCESS ? "AT" : "RT";
    }

    @Override
    public String fetchTokenBy(String loginId, TokenType type) {
        String suffix = generateSuffix(type);

        return jwtRedisTemplate.opsForValue().get(loginId + suffix);
    }

    @Override
    public void setToken(String key, String token, Duration duration, TokenType type) {
        String suffix = generateSuffix(type);

        jwtRedisTemplate.opsForValue().set(key + suffix, token, duration);
    }

    @Override
    public void deleteToken(String key, TokenType type) {
        String suffix = generateSuffix(type);

        jwtRedisTemplate.delete(key + suffix);
    }
}
