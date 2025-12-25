package kr.hhplus.be.server.pointhistory.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PointHistory {

    private Long id;
    private Long memberId;
    private Long pointId;
    private Long pointAmount;
    private LocalDateTime createdDate;
    private Long totalPoint;
    private State state;


    public static PointHistory of(Long id, Long memberId, Long pointId, Long pointAmount, LocalDateTime createdDate, Long totalPoint, State state) {
        return new PointHistory(id, memberId, pointId, pointAmount, createdDate, totalPoint, state);
    }

    public static PointHistory create(Long memberId, Long pointId, Long pointAmount, LocalDateTime createdDate, Long totalPoint, State state) {
        PointHistory pointHistory = new PointHistory();
        pointHistory.memberId = memberId;
        pointHistory.pointId = pointId;
        pointHistory.pointAmount = pointAmount;
        pointHistory.createdDate = createdDate;
        pointHistory.totalPoint = totalPoint;
        pointHistory.state = state;
        return pointHistory;
    }

    private PointHistory() {}

    private PointHistory(Long id, Long memberId, Long pointId, Long pointAmount, LocalDateTime createdDate, Long totalPoint, State state) {
        this.id = id;
        this.memberId = memberId;
        this.pointId = pointId;
        this.pointAmount = pointAmount;
        this.createdDate = createdDate;
        this.totalPoint = totalPoint;
        this.state = state;
    }

    public void assignId(Long id) {
        this.id = id;
    }
}
