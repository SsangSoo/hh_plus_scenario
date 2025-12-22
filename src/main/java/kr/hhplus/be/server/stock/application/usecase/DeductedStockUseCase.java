package kr.hhplus.be.server.stock.application.usecase;

import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;

public interface DeductedStockUseCase {
    StockResponse deductedStock(Long productId, Long quantity);
}
