package kr.hhplus.be.server.point.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointJpaRepository extends JpaRepository<PointJpaEntity,Long> {

    Optional<PointJpaEntity> findByIdAndRemovedFalse(Long id);
    Optional<PointJpaEntity> findByMemberIdAndRemovedFalse(Long memberId);

}
