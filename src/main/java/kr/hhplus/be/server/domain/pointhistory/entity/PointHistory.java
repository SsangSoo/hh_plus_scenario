package kr.hhplus.be.server.domain.pointhistory.entity;

import jakarta.persistence.*;
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

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "total_point", nullable = false)
    private Long totalPoint;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;


    enum State {

        CHARGE, USE, REFUND

    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getPointId() {
        return pointId;
    }

    public Long getPointAmount() {
        return pointAmount;
    }

    public State getState() {
        return state;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public Long getTotalPoint() {
        return totalPoint;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
