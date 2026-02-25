package kr.hhplus.be.server.payment.infrastructure.event;

import kr.hhplus.be.server.payment.domain.event.PaymentEvent;
import kr.hhplus.be.server.payment.infrastructure.kafka.PaymentKafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PaymentEventListenerTest {

    @Mock
    PaymentKafkaProducer paymentKafkaProducer;

    PaymentEventListener paymentEventListener;

    @BeforeEach
    void setUp() {
        paymentEventListener = new PaymentEventListener(paymentKafkaProducer);
    }

    @Test
    @DisplayName("결제 이벤트 수신 시 Kafka Producer로 전달한다")
    void onPaymentEvent_sendsToKafkaProducer() {
        // given
        PaymentEvent paymentEvent = new PaymentEvent(1L, 1L);

        // when
        paymentEventListener.onPaymentEvent(paymentEvent);

        // then
        then(paymentKafkaProducer).should(times(1)).send(paymentEvent);
    }
}