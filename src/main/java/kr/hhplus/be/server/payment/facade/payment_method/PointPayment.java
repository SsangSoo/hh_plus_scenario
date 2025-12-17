package kr.hhplus.be.server.payment.facade.payment_method;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.payment.facade.service.request.PaymentServiceRequest;
import kr.hhplus.be.server.point.entity.Point;
import kr.hhplus.be.server.point.repository.PointRepository;
import kr.hhplus.be.server.pointhistory.entity.PointHistory;
import kr.hhplus.be.server.pointhistory.entity.State;
import kr.hhplus.be.server.pointhistory.repository.PointHistoryRepository;
import kr.hhplus.be.server.pointhistory.service.request.RegisterPointHistoryRequest;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointPayment implements PaymentStrategy{

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public void pay(PaymentServiceRequest request) {
        // 포인트 찾기
        Point point = findPoint(request.memberId());

        // 포인트 차감
        point.use(request.totalAmount());

        // 포인트 차감 내역 생성
        PointHistory pointHistory = PointHistory.register(new RegisterPointHistoryRequest(request.memberId(), point.getId(), request.totalAmount(), point.getModifiedDate(), point.getPoint()), State.USE);
        pointHistoryRepository.save(pointHistory);
    }

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.POINT;
    }

    private Point findPoint(Long memberId) {
        return pointRepository.findPointByMemberIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER_POINT));
    }


}
