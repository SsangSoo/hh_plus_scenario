package kr.hhplus.be.server.pointhistory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistoryJpaEntity, Long> {

    List<PointHistoryJpaEntity> findAllByPointId(Long pointId);
}
