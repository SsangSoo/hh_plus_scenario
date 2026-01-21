package kr.hhplus.be.server.point.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointJpaRepository extends JpaRepository<PointJpaEntity,Long> {

    Optional<PointJpaEntity> findByIdAndRemovedFalse(Long id);
    Optional<PointJpaEntity> findByMemberIdAndRemovedFalse(Long memberId);

    // 분산락으로 동시성 제어 (X-Lock 비활성화)
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PointJpaEntity p where p.memberId = :memberId and p.removed = false")
    Optional<PointJpaEntity> findByMemberIdForUpdate(Long memberId);

}
