package kr.hhplus.be.server.point.infrastructure.persistence;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository jpa;

    @Override
    public Point save(Point point) {
        PointJpaEntity saved = jpa.save(PointJpaEntity.from(point));
        return saved.toDomain();
    }

    @Override
    public LocalDateTime update(Long id, Long point) {
        PointJpaEntity pointJpaEntity = jpa.findByIdAndRemovedFalse(id)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER_POINT));
        pointJpaEntity.update(point);
        return jpa.save(pointJpaEntity).getModifiedDate();
    }


    @Override
    public Point findByMemberId(Long memberId) {
        PointJpaEntity pointJpaEntity = jpa.findByMemberIdAndRemovedFalse(memberId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER_POINT));
        return pointJpaEntity.toDomain();
    }

    // 동시성 제어를 위한 Pessimistic Lock 적용
    @Override
    public Point findByMemberIdForUpdate(Long memberId) {
        PointJpaEntity pointJpaEntity = jpa.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER_POINT));
        return pointJpaEntity.toDomain();
    }

    @Override
    public void remove(Long id) {
        PointJpaEntity pointJpaEntity = jpa.findByIdAndRemovedFalse(id)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER_POINT));
        pointJpaEntity.remove();
    }
}
