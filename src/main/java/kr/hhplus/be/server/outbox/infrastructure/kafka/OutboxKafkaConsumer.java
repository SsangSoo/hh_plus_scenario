package kr.hhplus.be.server.outbox.infrastructure.kafka;

import kr.hhplus.be.server.common.config.kafka.KafkaTopicConfig;
import kr.hhplus.be.server.outbox.application.usecase.RemoveOutboxUseCase;
import kr.hhplus.be.server.outbox.domain.event.OutboxInfoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxKafkaConsumer {

    private final RemoveOutboxUseCase removeOutboxUseCase;

    @KafkaListener(
            topics = KafkaTopicConfig.OUTBOX_CLEANUP_TOPIC,
            groupId = "outbox-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(OutboxInfoEvent event) {
        log.info("Outbox 정리 이벤트 Kafka 수신 - paymentId: {}, orderId: {}", event.paymentId(), event.orderId());
        try {
            removeOutboxUseCase.remove(event.paymentId(), event.orderId());
            log.info("Outbox 정리 완료 - paymentId: {}, orderId: {}", event.paymentId(), event.orderId());
        } catch (Exception e) {
            log.error("Outbox 정리 실패 - paymentId: {}, orderId: {}, error: {}",
                    event.paymentId(), event.orderId(), e.getMessage());
            throw e;
        }
    }
}
