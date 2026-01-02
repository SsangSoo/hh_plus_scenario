package kr.hhplus.be.server.payment.domain.repository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.payment.domain.model.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    Payment retrievePayment(Long paymentId);

    Payment changeState(Payment payment);
}
