package com.ocare.domain.member.dto.response;

import com.ocare.domain.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String recordKey;
    private LocalDateTime createdAt;

    public static MemberResponse of(MemberEntity entity) {
        return MemberResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .recordKey(entity.getRecordKey())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
