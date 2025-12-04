package kr.hhplus.be.server.domain.order.controller.request;

import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.domain.order.service.request.OrderProductServiceRequest;

public record OrderProductRequest(

        @Positive
        Long productId,

        @Positive
        Long quantity
) {
    public OrderProductServiceRequest toServiceRequest() {
        return new OrderProductServiceRequest(productId, quantity);
    }
}