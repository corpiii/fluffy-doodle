package com.clean_light.server.jwt.dto;

import com.clean_light.server.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenInfo {
    private Long id;
    private String loginId;
    private String nickName;

    public static UserTokenInfo from(User user) {
        return new UserTokenInfo(user.getId(), user.getLoginId(), user.getNickName());
    }
}
