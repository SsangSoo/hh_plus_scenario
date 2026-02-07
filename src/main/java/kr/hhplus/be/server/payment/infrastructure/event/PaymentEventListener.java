package kr.hhplus.be.server.payment.infrastructure.event;

import kr.hhplus.be.server.payment.application.usecase.PaymentDataTransportUseCase;
import kr.hhplus.be.server.payment.domain.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentDataTransportUseCase paymentDataTransportUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentEvent(PaymentEvent paymentEvent) {
        paymentDataTransportUseCase.send(paymentEvent);
    }
}
