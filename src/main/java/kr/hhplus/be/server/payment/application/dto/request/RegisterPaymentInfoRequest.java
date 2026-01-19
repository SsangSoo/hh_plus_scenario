package kr.hhplus.be.server.payment.application.dto.request;


public record RegisterPaymentInfoRequest(
        Long orderId,
        Long totalAmount,
        Long memberId
) {
}
