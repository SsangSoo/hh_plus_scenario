package kr.hhplus.be.server.payment.infrastructure.kafka;

import kr.hhplus.be.server.common.config.kafka.KafkaTopicConfig;
import kr.hhplus.be.server.payment.domain.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public void send(PaymentEvent event) {
        CompletableFuture<SendResult<String, PaymentEvent>> future = kafkaTemplate.send(
                KafkaTopicConfig.PAYMENT_COMPLETED_TOPIC,
                String.valueOf(event.paymentId()),
                event
        );

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("결제 이벤트 Kafka 전송 실패 - paymentId: {}, error: {}", event.paymentId(), ex.getMessage());
            } else {
                log.info("결제 이벤트 Kafka 전송 성공 - paymentId: {}, partition: {}, offset: {}",
                        event.paymentId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
