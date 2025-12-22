package kr.hhplus.be.server.member.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity,Long> {

    Optional<MemberJpaEntity> findByIdAndRemovedFalse(Long id);
}
