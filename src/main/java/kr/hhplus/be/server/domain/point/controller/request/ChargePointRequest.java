package kr.hhplus.be.server.domain.point.controller.request;

import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.domain.point.service.request.ChargePoint;

public record ChargePointRequest(

        @Positive(message = "회원 Id가 올바르지 않은 값입니다. 다시 확인해주세요")
        Long memberId,

        @Positive(message = "충전 포인트 금액이 유효하지 않습니다. 다시 확인해주세요")
        Long chargePoint
) {
    public ChargePoint toChargePoint() {
        return new ChargePoint(memberId, chargePoint);
    }
}
