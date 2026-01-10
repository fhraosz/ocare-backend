package com.ocare.domain.member.controller

import com.ocare.common.response.ApiResponse
import com.ocare.domain.member.dto.LoginRequest
import com.ocare.domain.member.dto.LoginResponse
import com.ocare.domain.member.dto.MemberResponse
import com.ocare.domain.member.dto.SignUpRequest
import com.ocare.domain.member.service.MemberService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class MemberControllerTest extends Specification {

    MemberService memberService = Mock()

    @Subject
    MemberController memberController = new MemberController(memberService)

    def "회원가입 API 성공 테스트"() {
        given:
        SignUpRequest request = new SignUpRequest()
        request.name = "홍길동"
        request.nickname = "gildong"
        request.email = "gildong@test.com"
        request.password = "password123"

        MemberResponse memberResponse = MemberResponse.builder()
                .id(1L)
                .name("홍길동")
                .nickname("gildong")
                .email("gildong@test.com")
                .recordKey("test-record-key")
                .createdAt(LocalDateTime.now())
                .build()

        when:
        ResponseEntity<ApiResponse<MemberResponse>> result = memberController.signUp(request)

        then:
        1 * memberService.signUp(request) >> memberResponse

        result.statusCode == HttpStatus.CREATED
        result.body.success == true
        result.body.message == "회원가입이 완료되었습니다"
        result.body.data.id == 1L
        result.body.data.email == "gildong@test.com"
    }

    def "로그인 API 성공 테스트"() {
        given:
        LoginRequest request = new LoginRequest()
        request.email = "gildong@test.com"
        request.password = "password123"

        MemberResponse memberResponse = MemberResponse.builder()
                .id(1L)
                .name("홍길동")
                .nickname("gildong")
                .email("gildong@test.com")
                .recordKey("test-record-key")
                .createdAt(LocalDateTime.now())
                .build()

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken("jwt-token-string")
                .member(memberResponse)
                .build()

        when:
        ResponseEntity<ApiResponse<LoginResponse>> result = memberController.login(request)

        then:
        1 * memberService.login(request) >> loginResponse

        result.statusCode == HttpStatus.OK
        result.body.success == true
        result.body.message == "로그인 성공"
        result.body.data.accessToken == "jwt-token-string"
        result.body.data.member.id == 1L
    }
}
