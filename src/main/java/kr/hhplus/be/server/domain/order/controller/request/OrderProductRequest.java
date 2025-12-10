package kr.hhplus.be.server.domain.order.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.domain.orderproduct.service.request.OrderProductServiceRequest;

public record OrderProductRequest(

        @Positive(message = "유효하지 않은 값입니다. 상품 Id를 확인해주세요.")
        Long productId,

        @Positive
        Long quantity

) {
    public OrderProductServiceRequest toServiceRequest() {
        return new OrderProductServiceRequest(productId, quantity);
    }
}