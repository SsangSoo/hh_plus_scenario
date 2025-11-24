package kr.hhplus.be.server.domain.stock.controller.request;

import kr.hhplus.be.server.domain.stock.service.request.AddStock;

public record AddStockRequest(
        Long productId,
        Long addStock
) {
    public AddStock toAddStock() {
        return new AddStock(productId, addStock);
    }
}
