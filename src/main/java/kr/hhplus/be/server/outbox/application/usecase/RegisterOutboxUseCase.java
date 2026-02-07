package kr.hhplus.be.server.outbox.application.usecase;

import kr.hhplus.be.server.outbox.domain.model.Outbox;

public interface RegisterOutboxUseCase {

    Outbox register(Outbox outbox);
}
