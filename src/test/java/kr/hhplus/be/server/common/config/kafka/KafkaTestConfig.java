package kr.hhplus.be.server.common.config.kafka;

import kr.hhplus.be.server.coupon.domain.event.CouponIssueEvent;
import kr.hhplus.be.server.outbox.domain.event.OutboxInfoEvent;
import kr.hhplus.be.server.payment.domain.event.PaymentEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@TestConfiguration
public class KafkaTestConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
    }

    private Map<String, Object> producerConfigs(KafkaContainer kafkaContainer) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        return configProps;
    }

    @Bean
    @Primary
    public ProducerFactory<String, Object> producerFactory(KafkaContainer kafkaContainer) {
        return new DefaultKafkaProducerFactory<>(producerConfigs(kafkaContainer));
    }

    @Bean
    @Primary
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // 타입별 KafkaTemplate 빈 추가
    @Bean
    @Primary
    public KafkaTemplate<String, PaymentEvent> paymentKafkaTemplate(KafkaContainer kafkaContainer) {
        ProducerFactory<String, PaymentEvent> factory = new DefaultKafkaProducerFactory<>(producerConfigs(kafkaContainer));
        return new KafkaTemplate<>(factory);
    }

    @Bean
    @Primary
    public KafkaTemplate<String, CouponIssueEvent> couponKafkaTemplate(KafkaContainer kafkaContainer) {
        ProducerFactory<String, CouponIssueEvent> factory = new DefaultKafkaProducerFactory<>(producerConfigs(kafkaContainer));
        return new KafkaTemplate<>(factory);
    }

    @Bean
    @Primary
    public KafkaTemplate<String, OutboxInfoEvent> outboxKafkaTemplate(KafkaContainer kafkaContainer) {
        ProducerFactory<String, OutboxInfoEvent> factory = new DefaultKafkaProducerFactory<>(producerConfigs(kafkaContainer));
        return new KafkaTemplate<>(factory);
    }

    @Bean
    @Primary
    public ConsumerFactory<String, Object> consumerFactory(KafkaContainer kafkaContainer) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "kr.hhplus.be.server.*");
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    @Primary
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
