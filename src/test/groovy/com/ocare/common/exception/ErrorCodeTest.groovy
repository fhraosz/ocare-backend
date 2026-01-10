package com.ocare.common.exception

import org.springframework.http.HttpStatus
import spock.lang.Specification

class ErrorCodeTest extends Specification {

    def "ErrorCode enum 값 검증 테스트 - MEMBER 에러"() {
        expect:
        ErrorCode.MEMBER_EMAIL_DUPLICATE.code == "MEMBER_001"
        ErrorCode.MEMBER_EMAIL_DUPLICATE.message == "이미 사용 중인 이메일입니다"
        ErrorCode.MEMBER_EMAIL_DUPLICATE.status == HttpStatus.CONFLICT

        ErrorCode.MEMBER_NICKNAME_DUPLICATE.code == "MEMBER_002"
        ErrorCode.MEMBER_NICKNAME_DUPLICATE.status == HttpStatus.CONFLICT

        ErrorCode.MEMBER_NOT_FOUND.code == "MEMBER_003"
        ErrorCode.MEMBER_NOT_FOUND.status == HttpStatus.NOT_FOUND

        ErrorCode.MEMBER_PASSWORD_MISMATCH.code == "MEMBER_004"
        ErrorCode.MEMBER_PASSWORD_MISMATCH.status == HttpStatus.UNAUTHORIZED
    }

    def "ErrorCode enum 값 검증 테스트 - HEALTH 에러"() {
        expect:
        ErrorCode.HEALTH_DATA_PARSE_ERROR.code == "HEALTH_001"
        ErrorCode.HEALTH_DATA_PARSE_ERROR.status == HttpStatus.BAD_REQUEST

        ErrorCode.HEALTH_DATA_NOT_FOUND.code == "HEALTH_002"
        ErrorCode.HEALTH_DATA_NOT_FOUND.status == HttpStatus.NOT_FOUND

        ErrorCode.HEALTH_RECORD_KEY_INVALID.code == "HEALTH_003"
        ErrorCode.HEALTH_RECORD_KEY_INVALID.status == HttpStatus.BAD_REQUEST
    }

    def "ErrorCode enum 값 검증 테스트 - AUTH 에러"() {
        expect:
        ErrorCode.AUTH_UNAUTHORIZED.code == "AUTH_001"
        ErrorCode.AUTH_UNAUTHORIZED.status == HttpStatus.UNAUTHORIZED

        ErrorCode.AUTH_TOKEN_EXPIRED.code == "AUTH_002"
        ErrorCode.AUTH_TOKEN_EXPIRED.status == HttpStatus.UNAUTHORIZED

        ErrorCode.AUTH_TOKEN_INVALID.code == "AUTH_003"
        ErrorCode.AUTH_TOKEN_INVALID.status == HttpStatus.UNAUTHORIZED
    }

    def "ErrorCode enum 값 검증 테스트 - COMMON 에러"() {
        expect:
        ErrorCode.INVALID_INPUT.code == "COMMON_001"
        ErrorCode.INVALID_INPUT.status == HttpStatus.BAD_REQUEST

        ErrorCode.INTERNAL_SERVER_ERROR.code == "COMMON_002"
        ErrorCode.INTERNAL_SERVER_ERROR.status == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "CustomException 생성 테스트 - ErrorCode 기반"() {
        when:
        CustomException exception = CustomException.of(ErrorCode.MEMBER_EMAIL_DUPLICATE)

        then:
        exception.errorCode == ErrorCode.MEMBER_EMAIL_DUPLICATE
        exception.code == "MEMBER_001"
        exception.message == "이미 사용 중인 이메일입니다"
        exception.status == HttpStatus.CONFLICT
    }

    def "CustomException 생성 테스트 - 커스텀 메시지"() {
        when:
        CustomException exception = CustomException.of(ErrorCode.MEMBER_NOT_FOUND, "ID가 123인 회원을 찾을 수 없습니다")

        then:
        exception.errorCode == ErrorCode.MEMBER_NOT_FOUND
        exception.code == "MEMBER_003"
        exception.message == "ID가 123인 회원을 찾을 수 없습니다"
        exception.status == HttpStatus.NOT_FOUND
    }
}
