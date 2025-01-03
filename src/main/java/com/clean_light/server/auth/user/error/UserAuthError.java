package com.clean_light.server.user.error;

public enum UserAuthError {
    DUPLICATED_LOGIN_ID("아이디가 중복되었습니다."),
    DUPLICATED_NICK_NAME("닉네임이 중복되었습니다."),
    INVALID_LOGIN_ID("아이디가 잘못되었습니다."),
    INVALID_PASSWORD("비밀번호가 잘못되었습니다."),
    SESSION_EXPIRED("세션이 만료되었습니다."),
    USER_NOT_CONNECTED("접속중인 유저가 아닙니다."),
    USER_ALREADY_LOGGED_IN("이미 로그인 중인 유저입니다."),
    USER_NOT_EXIST("존재하지 않는 유저입니다.");

    private final String message;

    UserAuthError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
