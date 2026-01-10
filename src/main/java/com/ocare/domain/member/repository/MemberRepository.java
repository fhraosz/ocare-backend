package com.ocare.domain.member.repository;

import com.ocare.domain.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 Repository
 */
@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByEmail(String email);

    Optional<MemberEntity> findByRecordKey(String recordKey);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
