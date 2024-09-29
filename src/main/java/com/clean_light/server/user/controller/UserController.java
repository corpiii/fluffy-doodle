package com.clean_light.server.user.controller;

import com.clean_light.server.global.ApiResponse;
import com.clean_light.server.user.domain.User;
import com.clean_light.server.user.dto.UserAuthToken;
import com.clean_light.server.user.dto.UserJoinRequest;
import com.clean_light.server.user.dto.UserLoginRequest;
import com.clean_light.server.user.error.UserAuthException;
import com.clean_light.server.user.service.UserAuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserAuthService userAuthService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Void>> join(@Valid @RequestBody UserJoinRequest userJoinRequest) {
        String encodedPassword = passwordEncoder.encode(userJoinRequest.getPassword());

        User user = User.builder()
                .loginId(userJoinRequest.getLoginId())
                .password(encodedPassword)
                .email(userJoinRequest.getEmail())
                .nickName(userJoinRequest.getNickName())
                .build();

        try {
            userAuthService.join(user);
        } catch (UserAuthException e) {
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, null, e.getMessage());

            return ResponseEntity.badRequest().body(apiResponse);
        }

        ApiResponse<Void> apiResponse = new ApiResponse<>(true, null, "회원가입이 완료되었습니다.");

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@RequestBody UserLoginRequest userLoginRequest) throws JsonProcessingException {
        User user = User.builder()
                .loginId(userLoginRequest.loginId)
                .password(userLoginRequest.password)
                .build();

        try {
            UserAuthToken userAuthToken = userAuthService.login(user);
            ApiResponse<Void> apiResponse = new ApiResponse<>(true, null, "로그인 되었습니다.");

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + userAuthToken.getAccessToken())
                    .header("Refresh-Token", "Bearer " + userAuthToken.getRefreshToken())
                    .body(apiResponse);
        } catch (UserAuthException e) {
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, null, e.getMessage());

            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @DeleteMapping()
    public ResponseEntity<ApiResponse<Void>> delete(@RequestHeader("Authorization") String accessToken) {
        try {
            userAuthService.delete(accessToken);
        } catch (Exception e) {
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, null, e.getMessage());

            return ResponseEntity.badRequest().body(apiResponse);
        }

        ApiResponse<Void> apiResponse = new ApiResponse<>(true, null, "탈퇴 되었습니다.");

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String accessToken) {
        String newAccessToken = accessToken.substring(7);

        try {
            userAuthService.logout(newAccessToken);
        } catch (Exception e) {
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, null, e.getMessage());

            return ResponseEntity.badRequest().body(apiResponse);
        }

        ApiResponse<Void> apiResponse = new ApiResponse<>(true, null, "로그아웃 되었습니다.");

        return ResponseEntity.ok(apiResponse);
    }
}
