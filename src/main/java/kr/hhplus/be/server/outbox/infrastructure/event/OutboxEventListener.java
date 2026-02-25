package kr.hhplus.be.server.outbox.infrastructure.event;

import kr.hhplus.be.server.outbox.domain.event.OutboxInfoEvent;
import kr.hhplus.be.server.outbox.infrastructure.kafka.OutboxKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventListener {

    private final OutboxKafkaProducer outboxKafkaProducer;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDataTransportCompleted(OutboxInfoEvent event) {
        log.info("Outbox 정리 이벤트 수신 - Kafka로 전송 - paymentId: {}, orderId: {}", event.paymentId(), event.orderId());
        outboxKafkaProducer.send(event);
    }
}

