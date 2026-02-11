package kr.hhplus.be.server.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

/**
 * 테스트 환경에서 @Async를 동기적으로 실행하기 위한 설정
 * 비동기 이벤트 리스너가 동기적으로 실행되어 테스트 안정성 향상
 */
@TestConfiguration
@EnableAsync
public class TestAsyncConfig implements AsyncConfigurer {

    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        return new SyncTaskExecutor();
    }

    @Bean(name = "applicationTaskExecutor")
    public TaskExecutor applicationTaskExecutor() {
        return new SyncTaskExecutor();
    }

    @Override
    public Executor getAsyncExecutor() {
        return new SyncTaskExecutor();
    }
}
