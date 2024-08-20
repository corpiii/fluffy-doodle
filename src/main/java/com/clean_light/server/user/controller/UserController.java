package com.clean_light.server.user.controller;

import com.clean_light.server.user.domain.User;
import com.clean_light.server.user.dto.UserJoinRequest;
import com.clean_light.server.user.service.UserAuthService;
import com.clean_light.server.user.service.UserInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserAuthService userAuthService;
    private final UserInfoService userInfoService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody UserJoinRequest userJoinRequest) {
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

        return new ResponseEntity<>(userJoinRequest, HttpStatus.CREATED);
    }
}
