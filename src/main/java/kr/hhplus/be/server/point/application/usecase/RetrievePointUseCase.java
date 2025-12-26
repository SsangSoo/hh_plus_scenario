package kr.hhplus.be.server.point.application.usecase;

import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;

public interface RetrievePointUseCase {
    PointResponse retrieve(Long memberId);
}
