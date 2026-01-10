package com.ocare.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 정의
 * 도메인별 에러 코드 체계화
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common Errors (COMMON_XXX)
    INVALID_INPUT("COMMON_001", "입력값이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("COMMON_002", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    // Member Errors (MEMBER_XXX)
    MEMBER_EMAIL_DUPLICATE("MEMBER_001", "이미 사용 중인 이메일입니다", HttpStatus.CONFLICT),
    MEMBER_NICKNAME_DUPLICATE("MEMBER_002", "이미 사용 중인 닉네임입니다", HttpStatus.CONFLICT),
    MEMBER_NOT_FOUND("MEMBER_003", "회원을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    MEMBER_PASSWORD_MISMATCH("MEMBER_004", "이메일 또는 비밀번호가 일치하지 않습니다", HttpStatus.UNAUTHORIZED),

    // Health Errors (HEALTH_XXX)
    HEALTH_DATA_PARSE_ERROR("HEALTH_001", "건강 데이터 파싱에 실패했습니다", HttpStatus.BAD_REQUEST),
    HEALTH_DATA_NOT_FOUND("HEALTH_002", "건강 데이터를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    HEALTH_RECORD_KEY_INVALID("HEALTH_003", "유효하지 않은 recordKey입니다", HttpStatus.BAD_REQUEST),

    // Auth Errors (AUTH_XXX)
    AUTH_UNAUTHORIZED("AUTH_001", "인증이 필요합니다", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_EXPIRED("AUTH_002", "토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_INVALID("AUTH_003", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
