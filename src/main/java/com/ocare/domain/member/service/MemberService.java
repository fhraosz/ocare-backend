package com.ocare.domain.member.service;

import com.ocare.common.exception.CustomException;
import com.ocare.common.exception.ErrorCode;
import com.ocare.config.jwt.JwtTokenProvider;
import com.ocare.domain.member.dto.*;
import com.ocare.domain.member.entity.MemberEntity;
import com.ocare.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     */
    @Transactional
    public MemberResponse signUp(SignUpRequest request) {
        log.debug("회원가입 요청: email={}", request.getEmail());

        // 이메일 중복 검사
        if (memberRepository.existsByEmail(request.getEmail())) {
            log.error("이메일 중복: email={}", request.getEmail());
            throw CustomException.of(ErrorCode.MEMBER_EMAIL_DUPLICATE);
        }

        // 닉네임 중복 검사
        if (memberRepository.existsByNickname(request.getNickname())) {
            log.error("닉네임 중복: nickname={}", request.getNickname());
            throw CustomException.of(ErrorCode.MEMBER_NICKNAME_DUPLICATE);
        }

        // 회원 생성
        MemberEntity member;
        if (request.getRecordKey() != null && !request.getRecordKey().isBlank()) {
            member = MemberEntity.of(
                    request.getName(),
                    request.getNickname(),
                    request.getEmail(),
                    passwordEncoder.encode(request.getPassword()),
                    request.getRecordKey()
            );
        } else {
            member = MemberEntity.of(
                    request.getName(),
                    request.getNickname(),
                    request.getEmail(),
                    passwordEncoder.encode(request.getPassword())
            );
        }

        MemberEntity savedMember = memberRepository.save(member);
        log.info("회원가입 완료: id={}, email={}", savedMember.getId(), savedMember.getEmail());

        return MemberResponse.of(savedMember);
    }

    /**
     * 로그인
     */
    public LoginResponse login(LoginRequest request) {
        log.debug("로그인 요청: email={}", request.getEmail());

        // 회원 조회
        MemberEntity member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("존재하지 않는 회원: email={}", request.getEmail());
                    return CustomException.of(ErrorCode.MEMBER_PASSWORD_MISMATCH);
                });

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            log.error("비밀번호 불일치: email={}", request.getEmail());
            throw CustomException.of(ErrorCode.MEMBER_PASSWORD_MISMATCH);
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createToken(member.getEmail());
        log.info("로그인 성공: id={}, email={}", member.getId(), member.getEmail());

        return LoginResponse.of(accessToken, MemberResponse.of(member));
    }

    /**
     * 이메일로 회원 조회
     */
    public MemberEntity findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 회원: email={}", email);
                    return CustomException.of(ErrorCode.MEMBER_NOT_FOUND);
                });
    }

    /**
     * recordKey로 회원 조회
     */
    public MemberEntity findByRecordKey(String recordKey) {
        return memberRepository.findByRecordKey(recordKey)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 recordKey: recordKey={}", recordKey);
                    return CustomException.of(ErrorCode.MEMBER_NOT_FOUND);
                });
    }
}
