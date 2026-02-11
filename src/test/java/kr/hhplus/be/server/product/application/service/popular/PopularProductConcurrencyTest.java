package kr.hhplus.be.server.product.application.service.popular;

import kr.hhplus.be.server.config.SpringBootTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Redis Sorted Set 기반 인기 상품 랭킹의 동시성 테스트
 * ZINCRBY 연산의 원자성을 검증합니다.
 */
class PopularProductConcurrencyTest extends SpringBootTestSupport {

    private static final String POPULAR_PRODUCT_KEY = "SELL:PRODUCT:RANKING";

    @BeforeEach
    void setUp() {
        // 테스트 시작 전 Redis 정리 - 다른 테스트의 잔여 데이터 제거
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .flushAll();
    }

    @AfterEach
    void tearDown() {
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .flushAll();
    }

    @Test
    @DisplayName("동시에 같은 상품의 판매량을 증가시켜도 정확한 값이 저장된다")
    void 동시에_같은_상품의_판매량을_증가시켜도_정확한_값이_저장된다() throws InterruptedException {
        // given
        int threadCount = 100;
        Long productId = 1L;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // ZINCRBY 연산 - 원자적으로 점수 1 증가
                    redisUtil.incrementZSetScore(POPULAR_PRODUCT_KEY, String.valueOf(productId), 1);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Double score = redisUtil.getZSetScore(POPULAR_PRODUCT_KEY, String.valueOf(productId));
        assertThat(score).isEqualTo(100.0);
    }

    @Test
    @DisplayName("여러 상품에 동시 판매량 증가시 각각 정확한 값이 저장된다")
    void 여러_상품에_동시_판매량_증가시_각각_정확한_값이_저장된다() throws InterruptedException {
        // given
        int threadCount = 100;
        int productCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount * productCount);

        // when - 각 상품에 대해 100번씩 동시 증가
        for (int productId = 1; productId <= productCount; productId++) {
            final Long pid = (long) productId;
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        redisUtil.incrementZSetScore(POPULAR_PRODUCT_KEY, String.valueOf(pid), 1);
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await();
        executorService.shutdown();

        // then - 각 상품의 점수가 정확히 100인지 검증
        for (int productId = 1; productId <= productCount; productId++) {
            Double score = redisUtil.getZSetScore(POPULAR_PRODUCT_KEY, String.valueOf(productId));
            assertThat(score)
                    .as("상품 %d의 점수", productId)
                    .isEqualTo(100.0);
        }
    }

    @Test
    @DisplayName("동시 증가 후 랭킹 순서가 올바르게 유지된다")
    void 동시_증가_후_랭킹_순서가_올바르게_유지된다() throws InterruptedException {
        // given
        // 상품1: 10회, 상품2: 50회, 상품3: 30회 구매 시뮬레이션
        int[] purchaseCounts = {10, 50, 30};
        Long[] productIds = {1L, 2L, 3L};
        int totalOperations = 0;
        for (int count : purchaseCounts) {
            totalOperations += count;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(totalOperations);

        // when
        for (int i = 0; i < productIds.length; i++) {
            final Long productId = productIds[i];
            final int count = purchaseCounts[i];
            for (int j = 0; j < count; j++) {
                executorService.submit(() -> {
                    try {
                        redisUtil.incrementZSetScore(POPULAR_PRODUCT_KEY, String.valueOf(productId), 1);
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await();
        executorService.shutdown();

        // then
        // 점수 검증
        assertThat(redisUtil.getZSetScore(POPULAR_PRODUCT_KEY, "1")).isEqualTo(10.0);
        assertThat(redisUtil.getZSetScore(POPULAR_PRODUCT_KEY, "2")).isEqualTo(50.0);
        assertThat(redisUtil.getZSetScore(POPULAR_PRODUCT_KEY, "3")).isEqualTo(30.0);

        // 랭킹 순서 검증 (내림차순: 2 > 3 > 1)
        Set<String> ranking = redisUtil.getZSetReverseRange(POPULAR_PRODUCT_KEY, 0, 2);
        assertThat(ranking).containsExactly("2", "3", "1");
    }

    @Test
    @DisplayName("동시 증가와 조회가 섞여도 데이터 정합성이 유지된다")
    void 동시_증가와_조회가_섞여도_데이터_정합성이_유지된다() throws InterruptedException {
        // given
        int writeThreadCount = 50;
        int readThreadCount = 50;
        Long productId = 1L;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(writeThreadCount + readThreadCount);

        // when - 쓰기와 읽기를 동시에 수행
        // 쓰기 스레드
        for (int i = 0; i < writeThreadCount; i++) {
            executorService.submit(() -> {
                try {
                    redisUtil.incrementZSetScore(POPULAR_PRODUCT_KEY, String.valueOf(productId), 1);
                } finally {
                    latch.countDown();
                }
            });
        }

        // 읽기 스레드
        for (int i = 0; i < readThreadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 읽기 중에도 에러가 발생하지 않아야 함
                    redisUtil.getZSetScore(POPULAR_PRODUCT_KEY, String.valueOf(productId));
                    redisUtil.getZSetReverseRange(POPULAR_PRODUCT_KEY, 0, 9);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then - 최종 점수는 정확히 50이어야 함
        Double score = redisUtil.getZSetScore(POPULAR_PRODUCT_KEY, String.valueOf(productId));
        assertThat(score).isEqualTo(50.0);
    }
}
