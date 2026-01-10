package com.ocare.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 응답 DTO
 */
@Getter
@Builder
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private MemberResponse member;

    public static LoginResponse of(String accessToken, MemberResponse member) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .member(member)
                .build();
    }
}
