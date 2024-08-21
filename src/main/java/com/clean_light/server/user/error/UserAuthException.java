package com.clean_light.server.user.error;

public class UserAuthException extends RuntimeException {
    private final UserAuthError errorCode;

    public UserAuthException(UserAuthError errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public UserAuthError getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
