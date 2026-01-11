package com.ocare.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus status;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.status = errorCode.getStatus();
    }

    public CustomException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.status = errorCode.getStatus();
    }

    public static CustomException of(ErrorCode errorCode) {
        return new CustomException(errorCode);
    }

    public static CustomException of(ErrorCode errorCode, String customMessage) {
        return new CustomException(errorCode, customMessage);
    }

    public String getCode() {
        return errorCode.getCode();
    }
}
