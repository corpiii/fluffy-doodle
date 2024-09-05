package com.clean_light.server.mock;

import com.clean_light.server.jwt.domain.TokenType;
import com.clean_light.server.jwt.repository.TokenRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Repository(value = "blackListRedisRepository")
@Profile("test")
public class MockBlackListRedisRepository implements TokenRepository {
    public Map<String, String> datasource = new HashMap<>();

    @Override
    public String fetchTokenBy(String loginId, TokenType type) {
        String suffix = type == TokenType.ACCESS ? "AT" : "RT";

        return datasource.get(loginId + suffix);
    }

    @Override
    public void setToken(String key, String token, Duration duration, TokenType type) {
        String suffix = type == TokenType.ACCESS ? "AT" : "RT";

        datasource.put(key + suffix, token);
    }

    @Override
    public void deleteToken(String key, TokenType type) {
        String suffix = type == TokenType.ACCESS ? "AT" : "RT";

        datasource.remove(key + suffix);
    }
}