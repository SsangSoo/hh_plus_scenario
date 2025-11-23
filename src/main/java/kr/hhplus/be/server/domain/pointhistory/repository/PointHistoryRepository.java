package kr.hhplus.be.server.domain.pointhistory.repository;

import kr.hhplus.be.server.domain.pointhistory.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    List<PointHistory> findAllByPointId(Long pointId);
}
