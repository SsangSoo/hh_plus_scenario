package kr.hhplus.be.server.outbox.application.service;

import kr.hhplus.be.server.outbox.application.usecase.RegisterOutboxUseCase;
import kr.hhplus.be.server.outbox.domain.model.Outbox;
import kr.hhplus.be.server.outbox.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterOutboxService implements RegisterOutboxUseCase {

    private final OutboxRepository outboxRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Outbox register(Outbox outbox) {
        return outboxRepository.save(outbox);
    }
}
