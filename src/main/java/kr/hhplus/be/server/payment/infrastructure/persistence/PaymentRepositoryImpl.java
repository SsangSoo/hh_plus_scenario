package kr.hhplus.be.server.payment.infrastructure.persistence;

import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpa;

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity saved = jpa.save(PaymentJpaEntity.from(payment));
        return saved.toDomain();
    }
}
