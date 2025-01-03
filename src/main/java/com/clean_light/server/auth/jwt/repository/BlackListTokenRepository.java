package com.clean_light.server.jwt.repository;

import java.time.Duration;

public interface BlackListTokenRepository {
    boolean isBlackList(String token);
    String fetchBy(String key);
    void setToken(String key, String value, Duration duration);
    void deleteToken(String key);
}
