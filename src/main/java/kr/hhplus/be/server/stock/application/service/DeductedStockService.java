package kr.hhplus.be.server.stock.application.service;

import kr.hhplus.be.server.stock.application.usecase.DeductedStockUseCase;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 재고 차감 서비스
 *
 * 분산락을 통해 멀티 인스턴스 환경에서 재고 동시성을 제어합니다.
 * 여러 상품 주문 시 데드락 방지를 위해 productId 오름차순으로 Lock을 획득합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeductedStockService implements DeductedStockUseCase {

    private final RedissonClient redissonClient;
    private final DeductedStockTransactionService transactionService;

    @Override
    public List<StockResponse> deductedStock(Map<Long, Long> orderProductMap) {
        // 1. productId를 오름차순 정렬 (데드락 방지)
        List<Long> sortedProductIds = orderProductMap.keySet().stream()
                .sorted()
                .toList();

        List<RLock> locks = new ArrayList<>();

        try {
            // 2. 순서대로 분산락 획득
            for (Long productId : sortedProductIds) {
                RLock lock = redissonClient.getLock("stock:lock:" + productId);
                // 분산락 획득: 대기시간 10초, watchdog 자동 연장 (-1)
                boolean available = lock.tryLock(10, -1, TimeUnit.SECONDS);

                if (!available) {
                    log.warn("재고 Lock 획득 실패 - productId: {}", productId);
                    throw new IllegalStateException("재고 Lock 획득 실패: productId=" + productId);
                }
                locks.add(lock);
            }

            // 3. 트랜잭션 내에서 재고 차감
            return transactionService.deductedStockInternal(orderProductMap);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("재고 차감 중 인터럽트 발생", e);
        } finally {
            // 4. 역순으로 Lock 해제 (LIFO)
            Collections.reverse(locks);
            for (RLock lock : locks) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }
}
