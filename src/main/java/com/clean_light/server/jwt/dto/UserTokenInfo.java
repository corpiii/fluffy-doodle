package com.clean_light.server.jwt.dto;

import com.clean_light.server.user.domain.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenInfo {
    private String loginId;
    private String nickName;

    public static UserTokenInfo from(User user) {
        return new UserTokenInfo(user.getLoginId(), user.getNickName());
    }
}
