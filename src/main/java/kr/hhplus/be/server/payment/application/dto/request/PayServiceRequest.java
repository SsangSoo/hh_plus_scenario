package kr.hhplus.be.server.payment.application.dto.request;

public record PayServiceRequest(
        Long orderId,
        Long paymentId,
        Long discountApplyAmount,
        Long totalAmount,
        Long memberId
) {
}
