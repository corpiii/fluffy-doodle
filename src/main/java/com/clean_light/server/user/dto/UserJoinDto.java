package com.clean_light.server.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserJoinDto {
    @NotBlank(message = "ID는 필수 입력 사항입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,}$",
            message = "ID는 영어, 숫자로 이루어진 3자 이상이어야 합니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9])[a-zA-Z0-9!@#$%^*+=-]{8,}$",
            message = "비밀번호는 8자 이상이어야 하며, 숫자, 영어, 특수문자(!@#$%^*+=-)를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "닉네임은 필수 입력 사항입니다.")
    @Size(min = 2, message = "닉네임은 2자 이상이어야 합니다.")
    private String nickName;
}
