package com.clean_light.server.jwt.controller;

import com.clean_light.server.global.ApiResponse;
import com.clean_light.server.jwt.service.JwtService;
import com.clean_light.server.user.dto.UserAuthToken;
import com.clean_light.server.user.service.UserAuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jwt")
public class JwtController {
    private final JwtService jwtService;
    private final UserAuthService userAuthService;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refresh(@RequestHeader("Authorization") String accessToken, @RequestHeader("Refresh-Token") String refreshToken) {
        try {
            String trimmedAT = accessToken.substring(7);
            String trimmedRT = refreshToken.substring(7);
            UserAuthToken token = jwtService.refresh(trimmedAT, trimmedRT);

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken())
                    .header("Refresh-Token", "Bearer " + token.getRefreshToken())
                    .build();
        } catch (Exception e) {
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, null, e.getMessage());

            return ResponseEntity.badRequest().body(apiResponse);
        }
    }
}
