package kr.hhplus.be.server.payment.infrastructure.event;

import kr.hhplus.be.server.payment.application.usecase.PaymentDataTransportUseCase;
import kr.hhplus.be.server.payment.domain.event.PaymentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PaymentEventListenerTest {

    @Mock
    PaymentDataTransportUseCase paymentDataTransportUseCase;

    PaymentEventListener paymentEventListener;


    @BeforeEach
    void setUp() {
        paymentEventListener = new PaymentEventListener(paymentDataTransportUseCase);
    }

    @Test
    void onPaymentDataTransportEvent() {
        // given
        PaymentEvent paymentEvent = new PaymentEvent(1L, 1L);

        // when
        paymentEventListener.onPaymentEvent(paymentEvent);

        // then
        then(paymentDataTransportUseCase).should(times(1)).send(paymentEvent);

    }
}