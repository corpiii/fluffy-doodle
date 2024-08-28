package com.clean_light.server.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthInfo {
    private final String loginId;
    private final String nickName;
}
