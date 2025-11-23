package kr.hhplus.be.server.domain.point.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.point.service.request.ChargePoint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "POINT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    @Column(name = "id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "point", nullable = false)
    private Long point;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

    @Column(name = "deleted")
    private Boolean deleted;

    public static Point register(Long memberId) {
        Point point = new Point();

        point.memberId = memberId;
        point.point = 0L;

        point.createdDate = LocalDateTime.now();
        point.modifiedDate = point.createdDate;
        point.deleted = false;

        return point;
    }

    public void delete() {
        this.deleted = true;
    }


    /**
     * 추후 단위 혹은 정해진 값에 따라 로직 변경될 수 있음
     * @param chargePoint
     * @return
     */
    public Long charge(ChargePoint chargePoint) {
        point += chargePoint.point();
        return point;
    }



}
