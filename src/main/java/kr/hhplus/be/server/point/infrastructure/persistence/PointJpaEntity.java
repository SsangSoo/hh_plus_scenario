package kr.hhplus.be.server.point.infrastructure.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseEntity;
import kr.hhplus.be.server.point.domain.model.Point;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "POINT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointJpaEntity extends BaseEntity {

    @Column(name = "id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "point", nullable = false)
    private Long point;

    public void update(Long point) {
        this.point = point;
    }

    public static PointJpaEntity from(Point point) {
        PointJpaEntity pointJpaEntity = new PointJpaEntity();

        pointJpaEntity.memberId = point.getMemberId();
        pointJpaEntity.point = point.getPoint();

        pointJpaEntity.createdDate = LocalDateTime.now();
        pointJpaEntity.modifiedDate = pointJpaEntity.createdDate;
        pointJpaEntity.removed = false;

        return pointJpaEntity;
    }


    public Point toDomain() {
        return Point.of(
                id,
                memberId,
                point
        );
    }







}
