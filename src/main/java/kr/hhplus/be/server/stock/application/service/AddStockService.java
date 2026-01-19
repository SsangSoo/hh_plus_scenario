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
            boolean avaliable = lock.tryLock(3, 1, TimeUnit.SECONDS);
            if (!avaliable) {
                log.warn("재고 Lock 획득 실패 - productId: {}", request.productId());
                throw new IllegalStateException("재고 Lock 획득 실패: productId = " + request.productId());
            }
            return addStockTransactionService.addStock(request);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("재고 차감 중 인터럽트 발생", e);
        } finally {
            lock.unlock();
        }
    }
}