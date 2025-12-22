package kr.hhplus.be.server.point.domain.model;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import lombok.Getter;

@Getter
public class Point {

    private Long id;
    private Long memberId;
    private Long point;

    private Point() {}

    private Point(Long id, Long memberId, Long point) {
        this.id = id;
        this.memberId = memberId;
        this.point = point;
    }

    public static Point of(Long id, Long memberId, Long point) {
        return new Point(id, memberId, point);
    }

    public static Point create(Long memberId) {
        Point point = new Point();
        point.memberId = memberId;
        point.point = 0L;
        return point;
    }


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

    public void assignId(Long id) {
        this.id = id;
    }
}
