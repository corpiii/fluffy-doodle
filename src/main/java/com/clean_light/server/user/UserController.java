package com.clean_light.server.user;

import com.clean_light.server.user.dto.UserJoinDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/join")
    public ResponseEntity<UserJoinDto> join(@Valid @RequestBody UserJoinDto userDto) {
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

}
