package kr.hhplus.be.server.point.application.usecase;

import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;

public interface ChargePointUseCase {
    PointResponse charge(ChargePoint chargePoint);
}
