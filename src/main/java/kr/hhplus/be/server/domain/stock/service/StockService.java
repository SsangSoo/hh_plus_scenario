package kr.hhplus.be.server.domain.stock.service;

import kr.hhplus.be.server.domain.stock.entity.Stock;
import kr.hhplus.be.server.domain.stock.repository.StockRepository;
import kr.hhplus.be.server.domain.stock.service.request.AddStock;
import kr.hhplus.be.server.domain.stock.service.response.StockResponse;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public StockResponse addStock(AddStock request) {
        // 상품을 찾아온다.
        stockRepository.findProductIdByProductId(request.productId())
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_PRODUCT));

        // 재고를 찾아온다.
        Stock stock = stockRepository.findByProductIdAndDeletedFalse(request.productId())
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_STOCK));

        // 재고를 추가한다.
        stock.addStock(request.addStock());

        return StockResponse.from(stock);
    }

    @Transactional(readOnly = true)
    public StockResponse retrieveStock(Long productId) {
        // 상품을 찾아온다.
        stockRepository.findProductIdByProductId(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_PRODUCT));

        // 재고를 찾아온다.
        Stock stock = stockRepository.findByProductIdAndDeletedFalse(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_STOCK));

        // 재고를 추가한다.
        return StockResponse.from(stock);
    }

}
