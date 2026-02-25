package kr.hhplus.be.server.payment.infrastructure.event;

import kr.hhplus.be.server.payment.domain.event.PaymentEvent;
import kr.hhplus.be.server.payment.infrastructure.kafka.PaymentKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentKafkaProducer paymentKafkaProducer;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentEvent(PaymentEvent paymentEvent) {
        log.info("결제 이벤트 수신 - Kafka로 전송 - paymentId: {}", paymentEvent.paymentId());
        paymentKafkaProducer.send(paymentEvent);
    }
}
