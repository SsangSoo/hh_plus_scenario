package kr.hhplus.be.server.payment.facade.service.request;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;

public record PaymentServiceRequest(
        Long orderId,
        Long totalAmount,
        PaymentMethod paymentMethod,
        Long memberId
) {
}
