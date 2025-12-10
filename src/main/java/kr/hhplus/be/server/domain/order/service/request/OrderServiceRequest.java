package kr.hhplus.be.server.domain.order.service.request;

import kr.hhplus.be.server.domain.order.controller.request.PaymentMethod;
import kr.hhplus.be.server.domain.orderproduct.service.request.OrderProductServiceRequest;

public record OrderServiceRequest (
        Long memberId,
        OrderProductServiceRequest orderProductRequest,
        PaymentMethod paymentMethod
) {

}
