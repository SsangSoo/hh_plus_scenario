package kr.hhplus.be.server.stock.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import kr.hhplus.be.server.stock.domain.model.Stock;
import kr.hhplus.be.server.stock.application.dto.request.AddStock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class StockServiceTest extends SpringBootTestSupport {

    @Test
    @DisplayName("재고 차감 잘 수행되는 케이스")
    void deductedStockHappyCase() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService es = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);

        ProductResponse productResponse = registerProductUseCase.register(new RegisterProductServiceRequest("고급 볼펜", 1000L));

        addStockUseCase.addStock(new AddStock(productResponse.getId(), 30000L));

        // when
        for(int i = 0; i < threadCount; i++) {
            es.submit(() -> {
                try {
                    deductedStockUseCase.deductedStock(Map.of(productResponse.getId(), 3L));
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Stock findStock = stockRepository.findByProductId(productResponse.getId());
        Assertions.assertThat(findStock.getQuantity()).isEqualTo(29700L);
    }



    @Test
    @DisplayName("재고 차감시 재고를 차감할 수 없는 상태라면, 차감하지 않는다.")
    void deductedStock() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService es = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);

        ProductResponse productResponse = registerProductUseCase.register(new RegisterProductServiceRequest("고급 볼펜", 1000L));

        addStockUseCase.addStock(new AddStock(productResponse.getId(), 30L));

        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for(int i = 0; i < threadCount; i++) {
            es.submit(() -> {
                try {
                    deductedStockUseCase.deductedStock(Map.of(productResponse.getId(), 4L));
                } catch (BusinessLogicRuntimeException be) {
                    failCount.set(failCount.get() + 1);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Stock stock = stockRepository.findByProductId(productResponse.getId());
        Assertions.assertThat(stock.getQuantity()).isEqualTo(2L);
        Assertions.assertThat(failCount.get()).isEqualTo(3);
    }


}