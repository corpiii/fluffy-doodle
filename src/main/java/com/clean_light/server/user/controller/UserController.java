package com.clean_light.server.user.controller;

import com.clean_light.server.jwt.service.JwtService;
import com.clean_light.server.user.domain.User;
import com.clean_light.server.user.dto.UserAuthToken;
import com.clean_light.server.user.dto.UserJoinRequest;
import com.clean_light.server.user.dto.UserLoginRequest;
import com.clean_light.server.user.error.UserAuthException;
import com.clean_light.server.user.service.UserAuthService;
import com.clean_light.server.user.service.UserInfoService;
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
    public ResponseEntity<?> join(@Valid @RequestBody UserJoinRequest userJoinRequest) {
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
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) throws JsonProcessingException {
        User user = User.builder()
                .loginId(userLoginRequest.loginId)
                .password(userLoginRequest.password)
                .build();

        try {
            UserAuthToken userAuthToken = userAuthService.login(user);

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + userAuthToken.getAccessToken())
                    .header("Refresh-Token", "Bearer " + userAuthToken.getRefreshToken())
                    .body("로그인 되었습니다.");
        } catch (UserAuthException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping()
    public ResponseEntity delete(@RequestHeader("AUTHORIZATION") String accessToken) {
        try {
            userAuthService.delete(accessToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("탈퇴되었습니다.");
    }

    @PostMapping("/logout")
    public ResponseEntity logout(@RequestHeader("AUTHORIZATION") String accessToken) {
        String newAccessToken = accessToken.substring(7);

        try {
            userAuthService.logout(newAccessToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
