package kr.hhplus.be.server.stock.application.service;

import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.stock.application.dto.request.AddStock;
import kr.hhplus.be.server.stock.application.usecase.AddStockUseCase;
import kr.hhplus.be.server.stock.domain.model.Stock;
import kr.hhplus.be.server.stock.domain.repository.StockRepository;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddStockService implements AddStockUseCase {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Override
    @Transactional
    public StockResponse addStock(AddStock request) {
        // 상품을 확인한다.
        productRepository.findById(request.productId());
        // 재고를 확인한다.
        Stock stock = stockRepository.findByProductIdForUpdate(request.productId());
        stock.addStock(request.addStock());
        stockRepository.modify(stock);

        return StockResponse.from(stock);
    }


}
