package com.clean_light.server.user.controller;

import com.clean_light.server.user.dto.UserJoinRequest;
import com.clean_light.server.user.service.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserAuthService userAuthService;

    @PostMapping("/join")
    public ResponseEntity<UserJoinRequest> join(@Valid @RequestBody UserJoinRequest userDto) {
        String loginId = userDto.getLoginId();
        String password = userDto.getPassword();

        userAuthService.join(userDto);

        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

}
