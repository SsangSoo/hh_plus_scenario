package kr.hhplus.be.server.outbox.infrastructure;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.outbox.domain.model.Outbox;
import kr.hhplus.be.server.outbox.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository jpa;

    @Override
    public Outbox save(Outbox outbox) {
        OutboxJpaEntity outboxJpaEntity = OutboxJpaEntity.from(outbox);
        OutboxJpaEntity saved = jpa.save(outboxJpaEntity);
        return saved.toDomain();
    }

    @Override
    public Outbox paymentComplete(Long orderId) {
        OutboxJpaEntity outboxJpaEntity = jpa.findByOrderId(orderId).orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_OUTBOX));
        outboxJpaEntity.paymentComplete();
        return outboxJpaEntity.toDomain();
    }

}
