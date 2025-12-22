package kr.hhplus.be.server.stock.application.service;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.stock.application.usecase.RetrieveStockUseCase;
import kr.hhplus.be.server.stock.domain.model.Stock;
import kr.hhplus.be.server.stock.domain.repository.StockRepository;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetrieveStockService implements RetrieveStockUseCase {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Override
    @Transactional(readOnly = true)
    public StockResponse retrieveStock(Long productId) {
        // 상품을 찾아온다.
        Product product = productRepository.findById(productId);

        // 재고를 찾아온다.
        Stock stock = stockRepository.findByProductId(product.getId());

        // 재고를 추가한다.
        return StockResponse.from(stock);
    }

}
