package kr.hhplus.be.server.outbox.infrastructure.event;

import kr.hhplus.be.server.outbox.application.usecase.RemoveOutboxUseCase;
import kr.hhplus.be.server.outbox.domain.event.OutboxInfoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
@RequiredArgsConstructor
public class OutboxEventListener {

    private final RemoveOutboxUseCase removeOutboxUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDataTransportCompleted(OutboxInfoEvent event) {
        removeOutboxUseCase.remove(event.paymentId(), event.orderId());
    }

}

