package kr.hhplus.be.server.domain.pointhistory.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.pointhistory.service.request.RegisterPointHistoryRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "POINT_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

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

    public static PointHistory register(RegisterPointHistoryRequest request, State state) {
        PointHistory  pointHistory = new PointHistory();

        pointHistory.memberId = request.memberId();
        pointHistory.pointId = request.pointId();
        pointHistory.pointAmount = request.pointAmount();
        pointHistory.createdDate = request.createdDate();
        pointHistory.totalPoint = request.totalPoint();
        pointHistory.state = state;

        return pointHistory;
    }

}
