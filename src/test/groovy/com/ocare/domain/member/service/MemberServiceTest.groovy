package com.ocare.domain.member.service

import com.ocare.common.exception.CustomException
import com.ocare.common.exception.ErrorCode
import com.ocare.config.jwt.JwtTokenProvider
import com.ocare.domain.member.dto.request.LoginRequest
import com.ocare.domain.member.dto.request.SignUpRequest
import com.ocare.domain.member.dto.response.LoginResponse
import com.ocare.domain.member.dto.response.MemberResponse
import com.ocare.domain.member.entity.MemberEntity
import com.ocare.domain.member.repository.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification
import spock.lang.Subject

class MemberServiceTest extends Specification {

    MemberRepository memberRepository = Mock()
    PasswordEncoder passwordEncoder = Mock()
    JwtTokenProvider jwtTokenProvider = Mock()

    @Subject
    MemberService memberService = new MemberService(memberRepository, passwordEncoder, jwtTokenProvider)

    def "회원가입 성공 테스트"() {
        given:
        SignUpRequest request = new SignUpRequest()
        request.name = "홍길동"
        request.nickname = "gildong"
        request.email = "gildong@test.com"
        request.password = "password123"

        MemberEntity savedMember = MemberEntity.builder()
                .id(1L)
                .name("홍길동")
                .nickname("gildong")
                .email("gildong@test.com")
                .password("encodedPassword")
                .recordKey("test-record-key")
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build()

        when:
        MemberResponse result = memberService.signUp(request)

        then:
        1 * memberRepository.existsByEmail("gildong@test.com") >> false
        1 * memberRepository.existsByNickname("gildong") >> false
        1 * passwordEncoder.encode("password123") >> "encodedPassword"
        1 * memberRepository.save(_ as MemberEntity) >> savedMember

        result.id == 1L
        result.name == "홍길동"
        result.nickname == "gildong"
        result.email == "gildong@test.com"
    }

    def "회원가입 실패 테스트 - 이메일 중복"() {
        given:
        SignUpRequest request = new SignUpRequest()
        request.name = "홍길동"
        request.nickname = "gildong"
        request.email = "duplicate@test.com"
        request.password = "password123"

        when:
        memberService.signUp(request)

        then:
        1 * memberRepository.existsByEmail("duplicate@test.com") >> true
        0 * memberRepository.save(_ as MemberEntity)

        CustomException e = thrown(CustomException)
        e.errorCode == ErrorCode.MEMBER_EMAIL_DUPLICATE
        e.code == "MEMBER_001"
    }

    def "회원가입 실패 테스트 - 닉네임 중복"() {
        given:
        SignUpRequest request = new SignUpRequest()
        request.name = "홍길동"
        request.nickname = "duplicateNick"
        request.email = "gildong@test.com"
        request.password = "password123"

        when:
        memberService.signUp(request)

        then:
        1 * memberRepository.existsByEmail("gildong@test.com") >> false
        1 * memberRepository.existsByNickname("duplicateNick") >> true
        0 * memberRepository.save(_ as MemberEntity)

        CustomException e = thrown(CustomException)
        e.errorCode == ErrorCode.MEMBER_NICKNAME_DUPLICATE
        e.code == "MEMBER_002"
    }

    def "회원가입 성공 테스트 - recordKey 지정"() {
        given:
        SignUpRequest request = new SignUpRequest()
        request.name = "홍길동"
        request.nickname = "gildong"
        request.email = "gildong@test.com"
        request.password = "password123"
        request.recordKey = "custom-record-key"

        MemberEntity savedMember = MemberEntity.builder()
                .id(1L)
                .name("홍길동")
                .nickname("gildong")
                .email("gildong@test.com")
                .password("encodedPassword")
                .recordKey("custom-record-key")
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build()

        when:
        MemberResponse result = memberService.signUp(request)

        then:
        1 * memberRepository.existsByEmail("gildong@test.com") >> false
        1 * memberRepository.existsByNickname("gildong") >> false
        1 * passwordEncoder.encode("password123") >> "encodedPassword"
        1 * memberRepository.save(_ as MemberEntity) >> savedMember

        result.recordKey == "custom-record-key"
    }

    def "로그인 성공 테스트"() {
        given:
        LoginRequest request = new LoginRequest()
        request.email = "gildong@test.com"
        request.password = "password123"

        MemberEntity member = MemberEntity.builder()
                .id(1L)
                .name("홍길동")
                .nickname("gildong")
                .email("gildong@test.com")
                .password("encodedPassword")
                .recordKey("test-record-key")
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build()

        when:
        LoginResponse result = memberService.login(request)

        then:
        1 * memberRepository.findByEmail("gildong@test.com") >> Optional.of(member)
        1 * passwordEncoder.matches("password123", "encodedPassword") >> true
        1 * jwtTokenProvider.createToken("gildong@test.com") >> "jwt-token-string"

        result.accessToken == "jwt-token-string"
        result.member.id == 1L
        result.member.email == "gildong@test.com"
    }

    def "로그인 실패 테스트 - 존재하지 않는 이메일"() {
        given:
        LoginRequest request = new LoginRequest()
        request.email = "notfound@test.com"
        request.password = "password123"

        when:
        memberService.login(request)

        then:
        1 * memberRepository.findByEmail("notfound@test.com") >> Optional.empty()
        0 * passwordEncoder.matches(_, _)

        CustomException e = thrown(CustomException)
        e.errorCode == ErrorCode.MEMBER_PASSWORD_MISMATCH
        e.code == "MEMBER_004"
    }

    def "로그인 실패 테스트 - 비밀번호 불일치"() {
        given:
        LoginRequest request = new LoginRequest()
        request.email = "gildong@test.com"
        request.password = "wrongPassword"

        MemberEntity member = MemberEntity.builder()
                .id(1L)
                .name("홍길동")
                .nickname("gildong")
                .email("gildong@test.com")
                .password("encodedPassword")
                .recordKey("test-record-key")
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build()

        when:
        memberService.login(request)

        then:
        1 * memberRepository.findByEmail("gildong@test.com") >> Optional.of(member)
        1 * passwordEncoder.matches("wrongPassword", "encodedPassword") >> false
        0 * jwtTokenProvider.createToken(_)

        CustomException e = thrown(CustomException)
        e.errorCode == ErrorCode.MEMBER_PASSWORD_MISMATCH
        e.code == "MEMBER_004"
    }

    def "이메일로 회원 조회 성공 테스트"() {
        given:
        String email = "gildong@test.com"

        MemberEntity member = MemberEntity.builder()
                .id(1L)
                .name("홍길동")
                .nickname("gildong")
                .email("gildong@test.com")
                .password("encodedPassword")
                .recordKey("test-record-key")
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build()

        when:
        MemberEntity result = memberService.findByEmail(email)

        then:
        1 * memberRepository.findByEmail(email) >> Optional.of(member)

        result.id == 1L
        result.email == "gildong@test.com"
    }

    def "이메일로 회원 조회 실패 테스트 - 회원 없음"() {
        given:
        String email = "notfound@test.com"

        when:
        memberService.findByEmail(email)

        then:
        1 * memberRepository.findByEmail(email) >> Optional.empty()

        CustomException e = thrown(CustomException)
        e.errorCode == ErrorCode.MEMBER_NOT_FOUND
        e.code == "MEMBER_003"
    }

    def "recordKey로 회원 조회 성공 테스트"() {
        given:
        String recordKey = "test-record-key"

        MemberEntity member = MemberEntity.builder()
                .id(1L)
                .name("홍길동")
                .nickname("gildong")
                .email("gildong@test.com")
                .password("encodedPassword")
                .recordKey("test-record-key")
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build()

        when:
        MemberEntity result = memberService.findByRecordKey(recordKey)

        then:
        1 * memberRepository.findByRecordKey(recordKey) >> Optional.of(member)

        result.id == 1L
        result.recordKey == "test-record-key"
    }

    def "recordKey로 회원 조회 실패 테스트 - 회원 없음"() {
        given:
        String recordKey = "invalid-record-key"

        when:
        memberService.findByRecordKey(recordKey)

        then:
        1 * memberRepository.findByRecordKey(recordKey) >> Optional.empty()

        CustomException e = thrown(CustomException)
        e.errorCode == ErrorCode.MEMBER_NOT_FOUND
        e.code == "MEMBER_003"
    }
}
