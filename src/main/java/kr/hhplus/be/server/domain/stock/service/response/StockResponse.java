package kr.hhplus.be.server.domain.stock.service.response;

import kr.hhplus.be.server.domain.stock.entity.Stock;
import lombok.Getter;

@Getter
public class StockResponse {

    private Long productId;
    private Long quantity;

    public static StockResponse from(Stock stock) {
        StockResponse stockResponse = new StockResponse();
        stockResponse.productId = stock.getProductId();
        stockResponse.quantity = stock.getQuantity();
        return stockResponse;
    }
}
