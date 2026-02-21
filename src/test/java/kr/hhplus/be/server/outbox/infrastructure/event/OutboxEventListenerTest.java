package kr.hhplus.be.server.outbox.infrastructure.event;

import kr.hhplus.be.server.outbox.domain.event.OutboxInfoEvent;
import kr.hhplus.be.server.outbox.infrastructure.kafka.OutboxKafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class OutboxEventListenerTest {

    @Mock
    OutboxKafkaProducer outboxKafkaProducer;

    OutboxEventListener outboxEventListener;

    @BeforeEach
    void setUp() {
        outboxEventListener = new OutboxEventListener(outboxKafkaProducer);
    }

    @Test
    @DisplayName("OutboxInfoEvent 수신 시, Kafka Producer로 전달한다")
    void onRemoveOutboxEvent_sendsToKafkaProducer() {
        // given
        OutboxInfoEvent outboxInfoEvent = new OutboxInfoEvent(1L, 1L);

        // when
        outboxEventListener.onDataTransportCompleted(outboxInfoEvent);

        // then
        then(outboxKafkaProducer).should(times(1)).send(outboxInfoEvent);
    }
}