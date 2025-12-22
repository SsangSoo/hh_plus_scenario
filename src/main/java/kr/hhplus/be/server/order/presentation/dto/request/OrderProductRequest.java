package kr.hhplus.be.server.order.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.orderproduct.application.dto.request.OrderProductServiceRequest;

public record OrderProductRequest(

        @NotNull(message = "상품 Id는 필수입니다.")
        @Positive(message = "유효하지 않은 값입니다. 상품 Id를 확인해주세요.")
        Long productId,

        @NotNull(message = "주문 수량은 필수입니다.")
        @Positive(message = "주문 수량은 0보다 커야합니다. 확인해주세요")
        Long quantity

) {
    public OrderProductServiceRequest toOrderProductCommand() {
        return new OrderProductServiceRequest(productId, quantity);
    }
}