package kr.hhplus.be.server.stock.application.usecase;

import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;

import java.util.List;
import java.util.Map;

public interface DeductedStockUseCase {
    List<StockResponse> deductedStock(Map<Long, Long> orderProductMap);
}
