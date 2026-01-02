package kr.hhplus.be.server.payment.application.dto.request;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;

public record RegisterPaymentInfoRequest(
        Long orderId,
        Long totalAmount,
        PaymentMethod paymentMethod,
        Long memberId
) {
}
