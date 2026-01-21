package kr.hhplus.be.server.stock.application.service;

import kr.hhplus.be.server.stock.application.dto.request.AddStock;
import kr.hhplus.be.server.stock.application.usecase.AddStockUseCase;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddStockService implements AddStockUseCase {

    private final RedissonClient redissonClient;
    private final AddStockTransactionService addStockTransactionService;

    @Override
    public StockResponse addStock(AddStock request) {
        RLock lock = redissonClient.getLock("stock:lock:" + request.productId());
        try {
            // 분산락 획득: 대기시간 10초, watchdog 자동 연장 (-1)
            boolean available = lock.tryLock(10, -1, TimeUnit.SECONDS);
            if (!available) {
                log.warn("재고 Lock 획득 실패 - productId: {}", request.productId());
                throw new IllegalStateException("재고 Lock 획득 실패: productId = " + request.productId());
            }
            return addStockTransactionService.addStock(request);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("재고 추가 중 인터럽트 발생", e);
        } finally {
            // Lock 해제 (안전하게 처리)
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}