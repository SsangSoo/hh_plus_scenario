package kr.hhplus.be.server.coupon.infrastructure.kafka;

import kr.hhplus.be.server.common.config.kafka.KafkaTopicConfig;
import kr.hhplus.be.server.coupon.application.usecase.DecreaseCouponUseCase;
import kr.hhplus.be.server.coupon.domain.event.CouponIssueEvent;
import kr.hhplus.be.server.couponhistory.application.usecase.RegisterCouponHistoryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaConsumer {

    private final DecreaseCouponUseCase decreaseCouponUseCase;
    private final RegisterCouponHistoryUseCase registerCouponHistoryUseCase;

    @KafkaListener(
            topics = KafkaTopicConfig.COUPON_ISSUED_TOPIC,
            groupId = "coupon-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void consume(CouponIssueEvent event) {
        log.info("쿠폰 발행 이벤트 Kafka 수신 - couponId: {}, memberId: {}", event.couponId(), event.memberId());
        try {
            decreaseCouponUseCase.decrease(event.couponId());
            registerCouponHistoryUseCase.register(event.couponId(), event.memberId());
            log.info("쿠폰 처리 완료 - couponId: {}, memberId: {}", event.couponId(), event.memberId());
        } catch (Exception e) {
            log.error("쿠폰 처리 실패 - couponId: {}, memberId: {}, error: {}",
                    event.couponId(), event.memberId(), e.getMessage());
            throw e;
        }
    }
}
