package kr.hhplus.be.server.domain.payment.facade.service.request;

import kr.hhplus.be.server.domain.order.interfaces.web.request.PaymentMethod;

public record PaymentServiceRequest(
        Long orderId,
        Long totalAmount,
        PaymentMethod paymentMethod,
        Long memberId
) {
}
