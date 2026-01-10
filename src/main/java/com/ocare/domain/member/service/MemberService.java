package com.ocare.domain.member.service;

import com.ocare.common.exception.CustomException;
import com.ocare.config.jwt.JwtTokenProvider;
import com.ocare.domain.member.dto.*;
import com.ocare.domain.member.entity.Member;
import com.ocare.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 서비스
 * 회원가입, 로그인 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     */
    @Transactional
    public MemberResponse signUp(SignUpRequest request) {
        // 이메일 중복 검사
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw CustomException.conflict("이미 사용 중인 이메일입니다");
        }

        // 닉네임 중복 검사
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw CustomException.conflict("이미 사용 중인 닉네임입니다");
        }

        // 회원 생성
        Member member;
        if (request.getRecordKey() != null && !request.getRecordKey().isBlank()) {
            // 기존 recordKey 연동
            member = Member.withRecordKey()
                    .name(request.getName())
                    .nickname(request.getNickname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .recordKey(request.getRecordKey())
                    .buildWithRecordKey();
        } else {
            // 새 recordKey 자동 생성
            member = Member.builder()
                    .name(request.getName())
                    .nickname(request.getNickname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();
        }

        Member savedMember = memberRepository.save(member);
        return MemberResponse.from(savedMember);
    }

    /**
     * 로그인
     */
    public LoginResponse login(LoginRequest request) {
        // 회원 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> CustomException.unauthorized("이메일 또는 비밀번호가 일치하지 않습니다"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw CustomException.unauthorized("이메일 또는 비밀번호가 일치하지 않습니다");
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createToken(member.getEmail());

        return LoginResponse.of(accessToken, MemberResponse.from(member));
    }

    /**
     * 이메일로 회원 조회
     */
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> CustomException.notFound("회원을 찾을 수 없습니다"));
    }

    /**
     * recordKey로 회원 조회
     */
    public Member findByRecordKey(String recordKey) {
        return memberRepository.findByRecordKey(recordKey)
                .orElseThrow(() -> CustomException.notFound("회원을 찾을 수 없습니다"));
    }
}
