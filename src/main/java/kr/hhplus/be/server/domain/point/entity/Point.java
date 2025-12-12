package kr.hhplus.be.server.domain.point.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseTimeEntity;
import kr.hhplus.be.server.domain.point.service.request.ChargePoint;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "POINT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseTimeEntity {

    @Column(name = "id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "point", nullable = false)
    private Long point;

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


    public void use(Long totalAmount) {
        validationPoint(totalAmount);
        point -= totalAmount;
    }

    public void validationPoint(Long validationPoint) {
        if (point < validationPoint) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.POINT_IS_NOT_ENOUGH.getMessage());
        }
    }




}
