package kr.hhplus.be.server.outbox.infrastructure.event;

import kr.hhplus.be.server.outbox.application.usecase.RemoveOutboxUseCase;
import kr.hhplus.be.server.outbox.domain.event.OutboxInfoEvent;
import kr.hhplus.be.server.payment.domain.event.PaymentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class OutboxEventListenerTest {

    @Mock
    RemoveOutboxUseCase removeOutboxUseCase;

    OutboxEventListener outboxEventListener;


    @BeforeEach
    void setUp() {
        outboxEventListener = new OutboxEventListener(removeOutboxUseCase);
    }

    @Test
    void onRemoveOutboxEvent() {
        // given
        OutboxInfoEvent outboxInfoEvent = new OutboxInfoEvent(1L, 1L);

        // when
        outboxEventListener.onDataTransportCompleted(outboxInfoEvent);

        // then
        then(removeOutboxUseCase).should(times(1)).remove(outboxInfoEvent.paymentId(), outboxInfoEvent.orderId());
    }
}