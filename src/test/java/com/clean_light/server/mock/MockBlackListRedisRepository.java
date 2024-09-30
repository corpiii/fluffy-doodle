package com.clean_light.server.mock;

import com.clean_light.server.jwt.repository.BlackListTokenRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Repository(value = "blackListRedisRepository")
@Profile("test")
public class MockBlackListRedisRepository implements BlackListTokenRepository {
    public Map<String, String> datasource = new HashMap<>();

    @Override
    public boolean isBlackList(String token) {
        return datasource.get(token) != null;
    }

    @Override
    public String fetchBy(String key) {
        return datasource.get(key);
    }

    @Override
    public void setToken(String key, String value, Duration duration) {
        datasource.put(key, value);
    }

    @Override
    public void deleteToken(String key) {
        datasource.remove(key);
    }
}