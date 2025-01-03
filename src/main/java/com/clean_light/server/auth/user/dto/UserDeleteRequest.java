package com.clean_light.server.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserDeleteRequest {
    private final String loginId;
}
