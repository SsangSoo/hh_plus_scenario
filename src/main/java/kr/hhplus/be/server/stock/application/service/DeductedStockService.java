package kr.hhplus.be.server.stock.application.service;

import kr.hhplus.be.server.stock.application.usecase.DeductedStockUseCase;
import kr.hhplus.be.server.stock.domain.model.Stock;
import kr.hhplus.be.server.stock.domain.repository.StockRepository;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeductedStockService implements DeductedStockUseCase {

    private final StockRepository stockRepository;

    @Override
    @Transactional
    public StockResponse deductedStock(Long productId, Long quantity) {
        Stock stock = stockRepository.findByProductIdForDeduct(productId, quantity);

        stock.deductedStock(quantity);
        stockRepository.modify(stock);

        return StockResponse.from(stock);
    }
}
