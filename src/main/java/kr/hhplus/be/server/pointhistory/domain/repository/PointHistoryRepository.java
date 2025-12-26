package kr.hhplus.be.server.pointhistory.domain.repository;

import kr.hhplus.be.server.pointhistory.domain.model.PointHistory;

import java.util.List;

public interface PointHistoryRepository {

    PointHistory save(PointHistory pointHistory);

    List<PointHistory> findPointListByPointId(Long pointId);
}
