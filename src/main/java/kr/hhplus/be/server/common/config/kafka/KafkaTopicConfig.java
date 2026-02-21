package kr.hhplus.be.server.common.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String PAYMENT_COMPLETED_TOPIC = "payment-completed";
    public static final String COUPON_ISSUED_TOPIC = "coupon-issued";
    public static final String OUTBOX_CLEANUP_TOPIC = "outbox-cleanup";

    @Bean
    public NewTopic paymentCompletedTopic() {
        return TopicBuilder.name(PAYMENT_COMPLETED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic couponIssuedTopic() {
        return TopicBuilder.name(COUPON_ISSUED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic outboxCleanupTopic() {
        return TopicBuilder.name(OUTBOX_CLEANUP_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
