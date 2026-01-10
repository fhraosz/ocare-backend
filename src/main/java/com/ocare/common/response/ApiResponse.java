package com.ocare.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ocare.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

/**
 * API 공통 응답 포맷
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String errorCode;
    private final String message;
    private final T data;

    // 성공 응답 - 데이터만
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("성공")
                .data(data)
                .build();
    }

    // 성공 응답 - 메시지 + 데이터
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    // 에러 응답 - ErrorCode 기반
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    // 에러 응답 - ErrorCode + 커스텀 메시지
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode.getCode())
                .message(customMessage)
                .build();
    }

    // 에러 응답 - 코드 + 메시지
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .build();
    }

    // 기존 호환성을 위한 메서드 (deprecated)
    @Deprecated
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode("COMMON_002")
                .message(message)
                .build();
    }
}
