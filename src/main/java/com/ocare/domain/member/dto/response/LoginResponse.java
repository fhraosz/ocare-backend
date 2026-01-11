package com.ocare.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
