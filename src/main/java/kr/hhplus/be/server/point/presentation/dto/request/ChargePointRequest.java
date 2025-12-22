package kr.hhplus.be.server.point.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.point.application.dto.request.ChargePoint;

public record ChargePointRequest(

        @NotNull(message = "사용자 Id는 필수입니다.")
        @Positive(message = "회원 Id가 올바르지 않은 값입니다. 다시 확인해주세요")
        Long memberId,

        @NotNull(message = "포인트 충전시 충전 금액은 필수입니다.")
        @Positive(message = "충전 포인트 금액이 유효하지 않습니다. 다시 확인해주세요")
        Long chargePoint
) {
    public ChargePoint toChargePoint() {
        return new ChargePoint(memberId, chargePoint);
    }
}
