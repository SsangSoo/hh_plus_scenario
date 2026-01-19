package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.payment.application.usecase.RetrievePaymentUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetrievePaymentService implements RetrievePaymentUseCase {

    private final PaymentRepository paymentRepository;

    @Override
    public Payment retrievePayment(Long paymentId) {
        return paymentRepository.retrievePayment(paymentId);
    }
}
