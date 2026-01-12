package kr.hhplus.be.server.point.application.usecase;

import kr.hhplus.be.server.point.application.dto.request.UsePoint;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;

public interface UsePointUseCase {
    PointResponse use(UsePoint usePoint);
}
