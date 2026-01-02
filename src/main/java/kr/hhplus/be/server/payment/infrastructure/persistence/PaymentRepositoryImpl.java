package kr.hhplus.be.server.payment.infrastructure.persistence;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
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

    @Override
    public Payment retrievePayment(Long paymentId) {
        PaymentJpaEntity retrieved = jpa.findById(paymentId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_PAYMENT_INFO));
        return retrieved.toDomain();
    }

    @Override
    public Payment changeState(Payment payment) {
        PaymentJpaEntity retrieved = jpa.findById(payment.getId())
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_PAYMENT_INFO));
        retrieved.changeState(payment.getPaymentState());
        return jpa.save(retrieved).toDomain();
    }

}
