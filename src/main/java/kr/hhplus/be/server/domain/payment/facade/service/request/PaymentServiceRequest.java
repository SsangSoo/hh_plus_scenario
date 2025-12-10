package kr.hhplus.be.server.domain.payment.facade.service.request;

import kr.hhplus.be.server.domain.order.controller.request.PaymentMethod;

public record PaymentServiceRequest(
        Long orderId,
        Long totalAmount,
        PaymentMethod paymentMethod,
        Long memberId
) {
}
