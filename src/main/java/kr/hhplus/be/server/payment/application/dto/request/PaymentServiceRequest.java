package kr.hhplus.be.server.payment.application.dto.request;

public record PaymentServiceRequest(

        Long orderId,
        Long memberId,
        Long paymentId,
        Long couponId
) {
    public PaymentServiceRequest(Long orderId, Long memberId, Long paymentId, Long couponId) {
        this.orderId = orderId;
        this.memberId = memberId;
        this.paymentId = paymentId;
        this.couponId = couponId;
    }
}
