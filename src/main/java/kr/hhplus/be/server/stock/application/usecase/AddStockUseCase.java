package kr.hhplus.be.server.stock.application.usecase;

import kr.hhplus.be.server.stock.application.dto.request.AddStock;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;

public interface AddStockUseCase {
    StockResponse addStock(AddStock request);
}
