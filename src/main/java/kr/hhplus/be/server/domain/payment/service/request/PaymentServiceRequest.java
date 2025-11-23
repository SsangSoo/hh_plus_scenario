package kr.hhplus.be.server.domain.payment.service.request;

public record PaymentServiceRequest(
        Long orderId,
        Long totalAmount
) {
}
