package kr.hhplus.be.server.coupon.infrastructure.kafka;

import kr.hhplus.be.server.common.config.kafka.KafkaTestConfig;
import kr.hhplus.be.server.common.config.kafka.KafkaTopicConfig;
import kr.hhplus.be.server.coupon.domain.event.CouponIssueEvent;
import kr.hhplus.be.server.outbox.infrastructure.kafka.OutboxKafkaConsumer;
import kr.hhplus.be.server.payment.infrastructure.kafka.PaymentKafkaConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.KafkaContainer;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("test")
@Import(KafkaTestConfig.class)
class CouponKafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private KafkaContainer kafkaContainer;

    // Kafka Consumer Mock (테스트 중 실제 Consumer 비활성화)
    @MockBean
    private PaymentKafkaConsumer paymentKafkaConsumer;

    @MockBean
    private CouponKafkaConsumer couponKafkaConsumer;

    @MockBean
    private OutboxKafkaConsumer outboxKafkaConsumer;

    private Consumer<String, CouponIssueEvent> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-coupon-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "kr.hhplus.be.server.*");
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, CouponIssueEvent.class.getName());

        ConsumerFactory<String, CouponIssueEvent> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singletonList(KafkaTopicConfig.COUPON_ISSUED_TOPIC));
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    @DisplayName("쿠폰 발행 이벤트가 Kafka 토픽으로 정상 발행된다")
    void couponIssueEventPublishedToKafkaTopic() {
        // given
        CouponIssueEvent couponEvent = new CouponIssueEvent(1L, 100L);

        // when
        kafkaTemplate.send(KafkaTopicConfig.COUPON_ISSUED_TOPIC, String.valueOf(couponEvent.couponId()), couponEvent);

        // then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            ConsumerRecords<String, CouponIssueEvent> records = consumer.poll(Duration.ofMillis(1000));
            assertThat(records.count()).isGreaterThanOrEqualTo(1);

            ConsumerRecord<String, CouponIssueEvent> record = records.iterator().next();
            assertThat(record.key()).isEqualTo("1");
            assertThat(record.value().couponId()).isEqualTo(1L);
            assertThat(record.value().memberId()).isEqualTo(100L);
        });
    }

    @Test
    @DisplayName("동일 쿠폰에 대한 여러 발행 이벤트가 같은 파티션으로 전송된다")
    void sameCouponEventsRoutedToSamePartition() {
        // given
        Long sameCouponId = 1L;
        CouponIssueEvent event1 = new CouponIssueEvent(sameCouponId, 101L);
        CouponIssueEvent event2 = new CouponIssueEvent(sameCouponId, 102L);
        CouponIssueEvent event3 = new CouponIssueEvent(sameCouponId, 103L);

        // when
        kafkaTemplate.send(KafkaTopicConfig.COUPON_ISSUED_TOPIC, String.valueOf(event1.couponId()), event1);
        kafkaTemplate.send(KafkaTopicConfig.COUPON_ISSUED_TOPIC, String.valueOf(event2.couponId()), event2);
        kafkaTemplate.send(KafkaTopicConfig.COUPON_ISSUED_TOPIC, String.valueOf(event3.couponId()), event3);

        // then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            ConsumerRecords<String, CouponIssueEvent> records = consumer.poll(Duration.ofMillis(1000));
            assertThat(records.count()).isGreaterThanOrEqualTo(3);

            // 동일 키를 사용하므로 같은 파티션에 순서대로 전송됨
            int partition = -1;
            for (ConsumerRecord<String, CouponIssueEvent> record : records) {
                if (partition == -1) {
                    partition = record.partition();
                } else {
                    assertThat(record.partition()).isEqualTo(partition);
                }
            }
        });
    }
}
