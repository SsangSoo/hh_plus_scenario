package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.payment.application.usecase.ChangePaymentStateUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangePaymentStateService implements ChangePaymentStateUseCase {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void changeState(Payment payment) {
        paymentRepository.changeState(payment);
    }
}
