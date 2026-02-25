package kr.hhplus.be.server.payment.infrastructure.kafka;

import kr.hhplus.be.server.common.config.kafka.KafkaTopicConfig;
import kr.hhplus.be.server.payment.application.usecase.PaymentDataTransportUseCase;
import kr.hhplus.be.server.payment.domain.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaConsumer {

    private final PaymentDataTransportUseCase paymentDataTransportUseCase;

    @KafkaListener(
            topics = KafkaTopicConfig.PAYMENT_COMPLETED_TOPIC,
            groupId = "payment-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(PaymentEvent event) {
        log.info("결제 이벤트 Kafka 수신 - paymentId: {}, orderId: {}", event.paymentId(), event.orderId());
        try {
            paymentDataTransportUseCase.send(event);
            log.info("결제 데이터 전송 완료 - paymentId: {}", event.paymentId());
        } catch (Exception e) {
            log.error("결제 데이터 전송 실패 - paymentId: {}, error: {}", event.paymentId(), e.getMessage());
            throw e;
        }
    }
}
