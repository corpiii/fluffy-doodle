package com.clean_light.server.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserAuthToken {
    private final String accessToken;
    private final String refreshToken;

    public static UserAuthToken of(String accessToken, String refreshToken) {
        return new UserAuthToken(accessToken, refreshToken);
    }
}
