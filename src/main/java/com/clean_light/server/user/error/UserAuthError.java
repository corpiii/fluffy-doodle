package com.clean_light.server.user.error;

public enum UserAuthError {
    INVALID_LOGIN_ID("아이디가 잘못되었습니다."),
    INVALID_PASSWORD("비밀번호가 잘못되었습니다.");

    private final String message;

    UserAuthError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
