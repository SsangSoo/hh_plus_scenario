package kr.hhplus.be.server.domain.order.application.in.request;

import kr.hhplus.be.server.domain.order.interfaces.web.request.PaymentMethod;
import kr.hhplus.be.server.domain.orderproduct.service.request.OrderProductServiceRequest;

public record OrderServiceRequest (
        Long memberId,
        OrderProductServiceRequest orderProductRequest,
        PaymentMethod paymentMethod
) {

}
