package kr.hhplus.be.server.stock.application.usecase;

import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;

public interface RetrieveStockUseCase {
    StockResponse retrieveStock(Long productId);
}
