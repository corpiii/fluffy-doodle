package com.clean_light.server.jwt.dto;

import com.clean_light.server.user.domain.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenDTO {
    private String loginId;
    private String nickName;

    public static UserTokenDTO from(User user) {
        return new UserTokenDTO(user.getLoginId(), user.getNickName());
    }
}
