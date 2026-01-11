package com.ocare.domain.member.service;

import com.ocare.common.exception.CustomException;
import com.ocare.common.exception.ErrorCode;
import com.ocare.config.jwt.JwtTokenProvider;
import com.ocare.domain.member.dto.request.LoginRequest;
import com.ocare.domain.member.dto.request.SignUpRequest;
import com.ocare.domain.member.dto.response.LoginResponse;
import com.ocare.domain.member.dto.response.MemberResponse;
import com.ocare.domain.member.entity.MemberEntity;
import com.ocare.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입 처리
     */
    @Transactional
    public MemberResponse signUp(SignUpRequest request) {
        log.debug("회원가입 요청: email={}", request.getEmail());

        validateDuplicateEmail(request.getEmail());
        validateDuplicateNickname(request.getNickname());

        MemberEntity member = createMember(request);
        MemberEntity savedMember = memberRepository.save(member);

        log.info("회원가입 완료: id={}, email={}", savedMember.getId(), savedMember.getEmail());
        return MemberResponse.of(savedMember);
    }

    /**
     * 로그인 처리 및 JWT 토큰 발급
     */
    public LoginResponse login(LoginRequest request) {
        log.debug("로그인 요청: email={}", request.getEmail());

        MemberEntity member = findByEmailOrThrow(request.getEmail());
        validatePassword(request.getPassword(), member.getPassword(), request.getEmail());

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

    /**
     * 이메일 중복 검증
     */
    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            log.error("이메일 중복: email={}", email);
            throw CustomException.of(ErrorCode.MEMBER_EMAIL_DUPLICATE);
        }
    }

    /**
     * 닉네임 중복 검증
     */
    private void validateDuplicateNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            log.error("닉네임 중복: nickname={}", nickname);
            throw CustomException.of(ErrorCode.MEMBER_NICKNAME_DUPLICATE);
        }
    }

    /**
     * 회원 엔티티 생성
     */
    private MemberEntity createMember(SignUpRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        if (request.getRecordKey() != null && !request.getRecordKey().isBlank()) {
            return MemberEntity.of(
                    request.getName(),
                    request.getNickname(),
                    request.getEmail(),
                    encodedPassword,
                    request.getRecordKey()
            );
        }
        return MemberEntity.of(
                request.getName(),
                request.getNickname(),
                request.getEmail(),
                encodedPassword
        );
    }

    /**
     * 이메일로 회원 조회 (로그인용 - 비밀번호 불일치 에러 반환)
     */
    private MemberEntity findByEmailOrThrow(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 회원: email={}", email);
                    return CustomException.of(ErrorCode.MEMBER_PASSWORD_MISMATCH);
                });
    }

    /**
     * 비밀번호 일치 검증
     */
    private void validatePassword(String rawPassword, String encodedPassword, String email) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            log.error("비밀번호 불일치: email={}", email);
            throw CustomException.of(ErrorCode.MEMBER_PASSWORD_MISMATCH);
        }
    }
}
