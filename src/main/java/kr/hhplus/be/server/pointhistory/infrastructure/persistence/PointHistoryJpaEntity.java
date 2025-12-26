package kr.hhplus.be.server.pointhistory.infrastructure.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.pointhistory.domain.model.PointHistory;
import kr.hhplus.be.server.pointhistory.domain.model.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "POINT_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistoryJpaEntity {

    @Column(name = "id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "point_id", nullable = false)
    private Long pointId;

    @Column(name = "point_amount", nullable = false)
    private Long pointAmount;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "total_point", nullable = false)
    private Long totalPoint;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;


    public static PointHistoryJpaEntity from(PointHistory pointHistory) {
        PointHistoryJpaEntity pointHistoryJpaEntity = new PointHistoryJpaEntity();

        pointHistoryJpaEntity.memberId = pointHistory.getMemberId();
        pointHistoryJpaEntity.pointId = pointHistory.getPointId();
        pointHistoryJpaEntity.pointAmount = pointHistory.getPointAmount();
        pointHistoryJpaEntity.createdDate = pointHistory.getCreatedDate();
        pointHistoryJpaEntity.totalPoint = pointHistory.getTotalPoint();
        pointHistoryJpaEntity.state = pointHistory.getState();

        return pointHistoryJpaEntity;
    }

    public PointHistory toDomain() {
        return PointHistory.of(
                id,
                memberId,
                pointId,
                pointAmount,
                createdDate,
                totalPoint,
                state
        );
    }
}
