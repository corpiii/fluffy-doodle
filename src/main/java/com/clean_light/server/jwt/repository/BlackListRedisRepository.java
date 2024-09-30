package com.clean_light.server.jwt.repository;

import com.clean_light.server.jwt.domain.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@Profile("default")
@RequiredArgsConstructor
public class BlackListRedisRepository implements BlackListTokenRepository {
    private final StringRedisTemplate blackListRedisTemplate;

    @Override
    public boolean isBlackList(String token) {
        return Boolean.TRUE.equals(blackListRedisTemplate.hasKey(token));
    }

    @Override
    public String fetchBy(String key) {
        return blackListRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void setToken(String key, String value, Duration duration) {
        blackListRedisTemplate.opsForValue().set(key, value, duration);
    }

    @Override
    public void deleteToken(String key) {
        blackListRedisTemplate.delete(key);
    }
}
