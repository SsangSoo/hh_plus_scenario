package kr.hhplus.be.server.coupon.infrastructure.event;

import kr.hhplus.be.server.coupon.domain.event.CouponIssueEvent;
import kr.hhplus.be.server.coupon.infrastructure.kafka.CouponKafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CouponIssueEventListenerTest {

    @Mock
    CouponKafkaProducer couponKafkaProducer;

    CouponIssueEventListener couponIssueEventListener;

    @BeforeEach
    void setUp() {
        couponIssueEventListener = new CouponIssueEventListener(couponKafkaProducer);
    }

    @Test
    @DisplayName("CouponIssueEvent 수신 시, Kafka Producer로 전달한다")
    void 이벤트_수신시_kafka_producer_호출() {
        // given
        Long couponId = 1L;
        Long memberId = 2L;
        CouponIssueEvent event = new CouponIssueEvent(couponId, memberId);

        // when
        couponIssueEventListener.onCouponIssueCompleted(event);

        // then
        then(couponKafkaProducer).should(times(1)).send(event);
    }
}
