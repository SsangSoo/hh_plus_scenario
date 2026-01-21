package kr.hhplus.be.server.stock.application.service;

import kr.hhplus.be.server.stock.domain.model.Stock;
import kr.hhplus.be.server.stock.domain.repository.StockRepository;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 재고 차감 트랜잭션 서비스
 *
 * 분산락 획득 후 트랜잭션 내에서 재고 차감 로직을 수행합니다.
 * DB Pessimistic Lock과 병행하여 Defense in Depth 전략을 적용합니다.
 */
@Component
@RequiredArgsConstructor
public class DeductedStockTransactionService {

    private final StockRepository stockRepository;

    /**
     * 재고 차감 (트랜잭션 내부)
     *
     * @param orderProductMap 주문 상품 맵 (productId -> quantity)
     * @return 재고 차감 결과 리스트
     */
    @Transactional
    public List<StockResponse> deductedStockInternal(Map<Long, Long> orderProductMap) {
        List<StockResponse> responses = new ArrayList<>();

        for(Map.Entry<Long, Long> entry : orderProductMap.entrySet()){
            // DB Lock 획득 (Defense in Depth)
            Stock stock = stockRepository.findByProductIdForUpdate(entry.getKey());

            // 재고 차감 (도메인 로직)
            stock.deductedStock(entry.getValue());

            // 재고 업데이트
            stockRepository.modify(stock);

            responses.add(StockResponse.from(stock));
        }

        return responses;
    }
}
