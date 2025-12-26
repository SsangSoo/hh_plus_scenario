package kr.hhplus.be.server.payment.application.service.payment_method;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import kr.hhplus.be.server.pointhistory.domain.model.PointHistory;
import kr.hhplus.be.server.pointhistory.domain.model.State;
import kr.hhplus.be.server.pointhistory.domain.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PointPayment implements PaymentStrategy{

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public void pay(PaymentServiceRequest request) {
        // 포인트 찾기
        Point point = pointRepository.findByMemberId(request.memberId());
        // 포인트 차감
        point.use(request.totalAmount());
        LocalDateTime modifiedDate = pointRepository.update(point.getId(), point.getPoint());

        // 포인트 차감 내역 생성
        PointHistory pointHistory = PointHistory.create(request.memberId(), point.getId(), request.totalAmount(), modifiedDate, point.getPoint(), State.USE);
        pointHistoryRepository.save(pointHistory);
    }

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.POINT;
    }

}
