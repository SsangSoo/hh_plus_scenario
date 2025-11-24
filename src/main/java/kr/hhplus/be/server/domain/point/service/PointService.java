package kr.hhplus.be.server.domain.point.service;

import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.domain.point.service.request.ChargePoint;
import kr.hhplus.be.server.domain.point.service.response.PointResponse;
import kr.hhplus.be.server.domain.pointhistory.entity.PointHistory;
import kr.hhplus.be.server.domain.pointhistory.entity.State;
import kr.hhplus.be.server.domain.pointhistory.repository.PointHistoryRepository;
import kr.hhplus.be.server.domain.pointhistory.service.request.RegisterPointHistoryRequest;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;


    @Transactional
    public PointResponse charge(ChargePoint chargePoint) {
        // 회원 찾기
        pointRepository.findMemberIdByMemberId(chargePoint.memberId())
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER));

        // 포인트 충전
        Point point = pointRepository.findPointByMemberIdAndDeletedFalse(chargePoint.memberId())
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER_POINT));

        point.charge(chargePoint);

        // 포인트 충전 내역 저장
        RegisterPointHistoryRequest registerPointHistoryRequest = registerPointHistoryRequest(chargePoint, chargePoint.memberId(), point);
        PointHistory pointHistory = PointHistory.register(registerPointHistoryRequest, State.CHARGE);
        pointHistoryRepository.save(pointHistory);

        return PointResponse.from(point);
    }


    @Transactional(readOnly = true)
    public PointResponse retrieve(Long memberId) {
        // 회원 찾기
        pointRepository.findMemberIdByMemberId(memberId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER));

        // 회원 Id로 포인트 찾기
        Point point = pointRepository.findPointByMemberIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER_POINT));

        return PointResponse.from(point);
    }


    private RegisterPointHistoryRequest registerPointHistoryRequest(ChargePoint chargePoint, Long memberId, Point point) {
        return new RegisterPointHistoryRequest(
                memberId,
                point.getId(),
                chargePoint.point(),
                point.getModifiedDate(),
                point.getPoint());
    }

}
