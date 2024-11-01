package com.example.demo.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("404", "User not found"),
    EMAIL_ALREADY_EXISTS("409", "Email already exists"),
    VALIDATION_ERROR("400", "Validation error"),
    SYSTEM_ERROR("500", "System error");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
