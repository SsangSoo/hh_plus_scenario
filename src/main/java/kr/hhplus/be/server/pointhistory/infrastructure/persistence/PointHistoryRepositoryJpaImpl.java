package kr.hhplus.be.server.pointhistory.infrastructure.persistence;

import kr.hhplus.be.server.pointhistory.domain.model.PointHistory;
import kr.hhplus.be.server.pointhistory.domain.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryJpaImpl implements PointHistoryRepository {

    private final PointHistoryJpaRepository jpa;

    @Override
    public PointHistory save(PointHistory pointHistory) {
        PointHistoryJpaEntity saved = jpa.save(PointHistoryJpaEntity.from(pointHistory));
        return saved.toDomain();
    }

    @Override
    public List<PointHistory> findPointListByPointId(Long pointId) {
        List<PointHistoryJpaEntity> allByPointId = jpa.findAllByPointId(pointId);
        return allByPointId.stream()
                .map(PointHistoryJpaEntity::toDomain)
                .toList();

    }
}
