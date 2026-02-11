package kr.hhplus.be.server.outbox.application.service;

import kr.hhplus.be.server.outbox.application.usecase.RetrieveOutboxUseCase;
import kr.hhplus.be.server.outbox.domain.model.Outbox;
import kr.hhplus.be.server.outbox.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetrieveOutboxService implements RetrieveOutboxUseCase {

    private final OutboxRepository outboxRepository;

    @Override
    @Transactional(readOnly = true)
    public Outbox retrieve(Long paymentId, Long orderId) {
        return outboxRepository.retrieve(paymentId, orderId);
    }
}
