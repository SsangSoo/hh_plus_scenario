package kr.hhplus.be.server.coupon.infrastructure.event;

import kr.hhplus.be.server.coupon.domain.event.CouponIssueEvent;
import kr.hhplus.be.server.coupon.infrastructure.kafka.CouponKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssueEventListener {

    private final CouponKafkaProducer couponKafkaProducer;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCouponIssueCompleted(CouponIssueEvent event) {
        log.info("쿠폰 발행 이벤트 수신 - Kafka로 전송 - couponId: {}, memberId: {}", event.couponId(), event.memberId());
        couponKafkaProducer.send(event);
    }
}
