package com.ocare.domain.member.service;

import com.ocare.domain.member.entity.MemberEntity;
import com.ocare.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Spring Security UserDetailsService 구현
 * JWT 인증 시 사용자 정보 로드
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없습니다: email={}", email);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
                });

        return new User(
                member.getEmail(),
                member.getPassword(),
                Collections.emptyList()
        );
    }
}
