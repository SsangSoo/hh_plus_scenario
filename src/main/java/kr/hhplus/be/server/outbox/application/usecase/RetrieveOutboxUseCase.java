package kr.hhplus.be.server.outbox.application.usecase;

import kr.hhplus.be.server.outbox.domain.model.Outbox;

public interface RetrieveOutboxUseCase {

    Outbox retrieve(Long paymentId, Long orderId);

}
