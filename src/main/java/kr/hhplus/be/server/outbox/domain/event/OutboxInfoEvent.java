package kr.hhplus.be.server.outbox.domain.event;

public record OutboxInfoEvent(
        Long paymentId,
        Long orderId
) {
    public OutboxInfoEvent(Long paymentId, Long orderId) {
        this.paymentId = paymentId;
        this.orderId = orderId;
    }
}
