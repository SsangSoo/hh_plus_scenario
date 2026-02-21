package kr.hhplus.be.server.coupon.infrastructure.kafka;

import kr.hhplus.be.server.common.config.kafka.KafkaTopicConfig;
import kr.hhplus.be.server.coupon.domain.event.CouponIssueEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaProducer {

    private final KafkaTemplate<String, CouponIssueEvent> kafkaTemplate;

    public void send(CouponIssueEvent event) {
        CompletableFuture<SendResult<String, CouponIssueEvent>> future = kafkaTemplate.send(
                KafkaTopicConfig.COUPON_ISSUED_TOPIC,
                String.valueOf(event.couponId()),
                event
        );

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("쿠폰 발행 이벤트 Kafka 전송 실패 - couponId: {}, memberId: {}, error: {}",
                        event.couponId(), event.memberId(), ex.getMessage());
            } else {
                log.info("쿠폰 발행 이벤트 Kafka 전송 성공 - couponId: {}, memberId: {}, partition: {}, offset: {}",
                        event.couponId(),
                        event.memberId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
