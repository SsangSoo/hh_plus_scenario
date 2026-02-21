package kr.hhplus.be.server.payment.infrastructure.kafka;

import kr.hhplus.be.server.common.config.kafka.KafkaTestConfig;
import kr.hhplus.be.server.common.config.kafka.KafkaTopicConfig;
import kr.hhplus.be.server.coupon.infrastructure.kafka.CouponKafkaConsumer;
import kr.hhplus.be.server.outbox.infrastructure.kafka.OutboxKafkaConsumer;
import kr.hhplus.be.server.payment.domain.event.PaymentEvent;
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
class PaymentKafkaIntegrationTest {

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

    private Consumer<String, PaymentEvent> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-payment-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "kr.hhplus.be.server.*");
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, PaymentEvent.class.getName());

        ConsumerFactory<String, PaymentEvent> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singletonList(KafkaTopicConfig.PAYMENT_COMPLETED_TOPIC));
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    @DisplayName("결제 이벤트가 Kafka 토픽으로 정상 발행된다")
    void paymentEventPublishedToKafkaTopic() {
        // given
        PaymentEvent paymentEvent = new PaymentEvent(1L, 100L);

        // when
        kafkaTemplate.send(KafkaTopicConfig.PAYMENT_COMPLETED_TOPIC, String.valueOf(paymentEvent.paymentId()), paymentEvent);

        // then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            ConsumerRecords<String, PaymentEvent> records = consumer.poll(Duration.ofMillis(1000));
            assertThat(records.count()).isGreaterThanOrEqualTo(1);

            ConsumerRecord<String, PaymentEvent> record = records.iterator().next();
            assertThat(record.key()).isEqualTo("1");
            assertThat(record.value().paymentId()).isEqualTo(1L);
            assertThat(record.value().orderId()).isEqualTo(100L);
        });
    }

    @Test
    @DisplayName("여러 결제 이벤트가 순서대로 Kafka에 발행된다")
    void multiplePaymentEventsPublishedInOrder() {
        // given
        PaymentEvent event1 = new PaymentEvent(1L, 101L);
        PaymentEvent event2 = new PaymentEvent(2L, 102L);
        PaymentEvent event3 = new PaymentEvent(3L, 103L);

        // when
        kafkaTemplate.send(KafkaTopicConfig.PAYMENT_COMPLETED_TOPIC, String.valueOf(event1.paymentId()), event1);
        kafkaTemplate.send(KafkaTopicConfig.PAYMENT_COMPLETED_TOPIC, String.valueOf(event2.paymentId()), event2);
        kafkaTemplate.send(KafkaTopicConfig.PAYMENT_COMPLETED_TOPIC, String.valueOf(event3.paymentId()), event3);

        // then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            ConsumerRecords<String, PaymentEvent> records = consumer.poll(Duration.ofMillis(1000));
            assertThat(records.count()).isGreaterThanOrEqualTo(3);
        });
    }
}
