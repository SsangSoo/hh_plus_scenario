package kr.hhplus.be.server.outbox.infrastructure.kafka;

import kr.hhplus.be.server.common.config.kafka.KafkaTopicConfig;
import kr.hhplus.be.server.outbox.domain.event.OutboxInfoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxKafkaProducer {

    private final KafkaTemplate<String, OutboxInfoEvent> kafkaTemplate;

    public void send(OutboxInfoEvent event) {
        CompletableFuture<SendResult<String, OutboxInfoEvent>> future = kafkaTemplate.send(
                KafkaTopicConfig.OUTBOX_CLEANUP_TOPIC,
                String.valueOf(event.paymentId()),
                event
        );

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Outbox 정리 이벤트 Kafka 전송 실패 - paymentId: {}, orderId: {}, error: {}",
                        event.paymentId(), event.orderId(), ex.getMessage());
            } else {
                log.info("Outbox 정리 이벤트 Kafka 전송 성공 - paymentId: {}, orderId: {}, partition: {}, offset: {}",
                        event.paymentId(),
                        event.orderId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
