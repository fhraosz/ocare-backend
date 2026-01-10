package com.ocare.domain.member.service

import com.ocare.domain.member.entity.MemberEntity
import com.ocare.domain.member.repository.MemberRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class CustomUserDetailsServiceTest extends Specification {

    MemberRepository memberRepository = Mock()

    @Subject
    CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(memberRepository)

    def "사용자 정보 로드 성공 테스트"() {
        given:
        String email = "gildong@test.com"

        MemberEntity member = MemberEntity.builder()
                .id(1L)
                .name("홍길동")
                .nickname("gildong")
                .email(email)
                .password("encodedPassword")
                .recordKey("test-record-key")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()

        when:
        UserDetails result = customUserDetailsService.loadUserByUsername(email)

        then:
        1 * memberRepository.findByEmail(email) >> Optional.of(member)

        result != null
        result.username == email
        result.password == "encodedPassword"
        result.authorities.size() == 1
        result.authorities[0].authority == "ROLE_USER"
    }

    def "사용자 정보 로드 실패 테스트 - 존재하지 않는 이메일"() {
        given:
        String email = "notfound@test.com"

        when:
        customUserDetailsService.loadUserByUsername(email)

        then:
        1 * memberRepository.findByEmail(email) >> Optional.empty()

        thrown(UsernameNotFoundException)
    }

    def "사용자 권한 확인 테스트"() {
        given:
        String email = "gildong@test.com"

        MemberEntity member = MemberEntity.builder()
                .id(1L)
                .name("홍길동")
                .nickname("gildong")
                .email(email)
                .password("encodedPassword")
                .recordKey("test-record-key")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()

        when:
        UserDetails result = customUserDetailsService.loadUserByUsername(email)

        then:
        1 * memberRepository.findByEmail(email) >> Optional.of(member)

        result.isAccountNonExpired()
        result.isAccountNonLocked()
        result.isCredentialsNonExpired()
        result.isEnabled()
    }
}
