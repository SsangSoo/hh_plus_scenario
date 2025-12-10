package kr.hhplus.be.server.domain.stock.service;

import kr.hhplus.be.server.domain.product.entity.Product;
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
        verifyProductExist(request.productId());

        // 재고를 찾아온다.
        Stock stock = findStockByProductId(request.productId());

        // 재고를 추가한다.
        stock.addStock(request.addStock());

        return StockResponse.from(stock);
    }

    @Transactional
    public StockResponse deductedStock(Long productId, Long quantity) {
        Stock stock = findStockForUpdate(productId, quantity);
        stock.deductedStock(quantity);
        return StockResponse.from(stock);
    }

    @Transactional(readOnly = true)
    public StockResponse retrieveStock(Long productId) {
        // 상품을 찾아온다.
        verifyProductExist(productId);

        // 재고를 찾아온다.
        Stock stock = findStockByProductId(productId);

        // 재고를 추가한다.
        return StockResponse.from(stock);
    }



    private Stock findStockByProductId(Long productId) {
        return stockRepository.findByProductIdAndDeletedFalse(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_STOCK));
    }

    private Stock findStockForUpdate(Long productId, Long quantity) {
        return stockRepository.findByProductIdForUpdate(productId, quantity)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_STOCK));
    }

    private void verifyProductExist(Long productId) {
        stockRepository.findProductIdByProductId(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_PRODUCT));
    }
    
    

}
