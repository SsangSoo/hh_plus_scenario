package kr.hhplus.be.server.member.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity,Long> {

    Optional<MemberJpaEntity> findByIdAndDeletedFalse(Long id);
}
