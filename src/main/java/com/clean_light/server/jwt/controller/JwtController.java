package com.clean_light.server.jwt.controller;

import com.clean_light.server.jwt.dto.UserTokenInfo;
import com.clean_light.server.jwt.service.JwtService;
import com.clean_light.server.user.dto.UserAuthToken;
import com.clean_light.server.user.service.UserAuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/jwt")
public class JwtController {
    private final JwtService jwtService;
    private final UserAuthService userAuthService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String accessToken, @RequestHeader("Refresh-Token") String refreshToken) throws JsonProcessingException {
        UserAuthToken token = jwtService.refresh(accessToken, refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken())
                .header("Refresh-Token", "Bearer " + token.getRefreshToken())
                .build();
    }
}
