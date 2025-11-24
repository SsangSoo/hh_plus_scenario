package kr.hhplus.be.server.domain.order.controller.request;

import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.domain.order.service.request.OrderServiceRequest;

public record OrderRequest(

        @Positive
        Long memberId,
        OrderProductRequest orderProductRequest
) {

    public OrderServiceRequest toServiceRequest() {
        return new OrderServiceRequest(memberId, orderProductRequest.toServiceRequest());
    }


}
