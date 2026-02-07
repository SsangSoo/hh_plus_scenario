package kr.hhplus.be.server.outbox.domain.repository;

import kr.hhplus.be.server.outbox.domain.model.Outbox;

public interface OutboxRepository {

    Outbox save(Outbox outbox);

    Outbox paymentComplete(Long orderId);

    Outbox retrieve(Long orderId);

    void remove(Long paymentId, Long orderId);
}
