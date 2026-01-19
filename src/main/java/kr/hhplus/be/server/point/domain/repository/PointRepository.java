package kr.hhplus.be.server.point.domain.repository;

import kr.hhplus.be.server.point.domain.model.Point;

import java.time.LocalDateTime;

public interface PointRepository {

    Point findByMemberId(Long memberId);

    // 동시성 제어를 위한 Pessimistic Lock 적용
    Point findByMemberIdForUpdate(Long memberId);

    Point save(Point point);

    LocalDateTime modify(Long id, Long point);

    void remove(Long memberId);
}
