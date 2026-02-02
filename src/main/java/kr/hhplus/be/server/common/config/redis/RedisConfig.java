package kr.hhplus.be.server.common.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * StringRedisTemplate 를 사용하기 위한 Config 설정
 * StringRedisTemplate 을 사용하는 이유 : 직렬화 시간 및 네트워크 전송량이 가장 빠르다고 생각하여 해당 내용 채택
 */
@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

}
