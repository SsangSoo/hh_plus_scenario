package kr.hhplus.be.server.payment.repository;

import kr.hhplus.be.server.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
