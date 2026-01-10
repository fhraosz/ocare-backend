package com.ocare.domain.member.repository;

import com.ocare.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 Repository
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByRecordKey(String recordKey);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
