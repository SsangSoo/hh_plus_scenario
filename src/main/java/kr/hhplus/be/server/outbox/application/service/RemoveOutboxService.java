package kr.hhplus.be.server.outbox.application.service;

import kr.hhplus.be.server.outbox.application.usecase.RemoveOutboxUseCase;
import kr.hhplus.be.server.outbox.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoveOutboxService implements RemoveOutboxUseCase {

    private final OutboxRepository outboxRepository;

    @Override
    @Transactional
    public void remove(Long paymentId, Long orderId) {
        outboxRepository.remove(paymentId, orderId);
    }
}
