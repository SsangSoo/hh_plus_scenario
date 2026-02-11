package kr.hhplus.be.server.outbox.application.usecase;

public interface RemoveOutboxUseCase {

    void remove(Long paymentId, Long orderId);
}
