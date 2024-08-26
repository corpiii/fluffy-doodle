package com.clean_light.server.user.controller;

import com.clean_light.server.user.domain.User;
import com.clean_light.server.user.dto.UserDeleteRequest;
import com.clean_light.server.user.dto.UserJoinRequest;
import com.clean_light.server.user.dto.UserLoginRequest;
import com.clean_light.server.user.error.UserAuthError;
import com.clean_light.server.user.error.UserAuthException;
import com.clean_light.server.user.service.UserAuthService;
import com.clean_light.server.user.service.UserInfoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserAuthService userAuthService;
    private final UserInfoService userInfoService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserJoinRequest userJoinRequest) {
        String loginId = userJoinRequest.getLoginId();
        String encodedPassword = passwordEncoder.encode(userJoinRequest.getPassword());

        if (userInfoService.isExistLoginId(loginId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 아이디 입니다.");
        }

        if (userInfoService.isExistNickName(userJoinRequest.getNickName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 닉네임 입니다.");
        }

        User user = User.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .email(userJoinRequest.getEmail())
                .nickName(userJoinRequest.getNickName())
                .build();

        userAuthService.join(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) {
        User user = User.builder()
                .loginId(userLoginRequest.loginId)
                .password(userLoginRequest.password)
                .build();

        try {
            int expired = 30 * 60;
            String sessionId = userAuthService.login(user, expired);
            Cookie cookie = new Cookie("SESSIONID", sessionId);

            cookie.setHttpOnly(true);
            cookie.setMaxAge(expired);
            cookie.setPath("/");
            cookie.setSecure(true);
            cookie.setAttribute("SameSite", "None");

            response.addCookie(cookie);

            return ResponseEntity.ok("로그인 되었습니다.");

        } catch (UserAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @DeleteMapping()
    public ResponseEntity delete(@CookieValue("SESSIONID") String sessionId) {
        try {
            userAuthService.deleteUserBySessionId(sessionId);
        } catch (UserAuthException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("탈퇴되었습니다.");
    }

    @PostMapping("/logout")
    public ResponseEntity logout(@CookieValue("SESSIONID") String sessionId) {
        try {
            userAuthService.logout(sessionId);
        } catch (UserAuthException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @GetMapping("/me")
    public ResponseEntity user(@CookieValue("SESSIONID") String sessionId) {
        User user = userAuthService.fetchUserBySessionId(sessionId);

        return ResponseEntity.ok(user);
    }
}
