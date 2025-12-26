package kr.hhplus.be.server.point.domain.repository;

import kr.hhplus.be.server.point.domain.model.Point;

import java.time.LocalDateTime;

public interface PointRepository {

    Point findByMemberId(Long memberId);

    Point save(Point point);

    LocalDateTime update(Long id, Long point);

    void remove(Long memberId);
}
