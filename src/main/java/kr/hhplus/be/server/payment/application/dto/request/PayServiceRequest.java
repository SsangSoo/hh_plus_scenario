package kr.hhplus.be.server.payment.application.dto.request;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;

public record PayServiceRequest(
        Long orderId,
        Long paymentId,
        Long discountApplyAmount,
        Long totalAmount,
        PaymentMethod paymentMethod,
        Long memberId
) {
}
