package kr.hhplus.be.server.stock.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.stock.service.request.AddStock;

public record AddStockRequest(

        @NotNull(message = "상품 Id는 필수입니다.")
        @Positive(message = "상품 Id가 유효하지 않습니다")
        Long productId,

        @NotNull(message = "재고 충전시 재고 충전 값은 필수입니다.")
        @Positive(message = "재고 충전 값은 0보다 커야합니다.")
        Long addStock
) {
    public AddStock toAddStock() {
        return new AddStock(productId, addStock);
    }
}
