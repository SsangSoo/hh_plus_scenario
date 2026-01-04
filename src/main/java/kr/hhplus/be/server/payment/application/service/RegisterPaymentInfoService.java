package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.payment.application.usecase.RegisterPaymentInfoUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.payment.application.dto.request.RegisterPaymentInfoRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterPaymentInfoService implements RegisterPaymentInfoUseCase {

    private final PaymentRepository paymentRepository;


    @Override
    @Transactional
    public PaymentResponse registerPaymentInfo(RegisterPaymentInfoRequest request) {
        // 결제 정보 저장
        Payment payment = paymentRepository.save(Payment.create(request.orderId(), request.totalAmount(), request.paymentMethod()));

        return PaymentResponse.from(payment);
    }



}
