package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.outbox.domain.model.Outbox;
import kr.hhplus.be.server.outbox.domain.repository.OutboxRepository;
import kr.hhplus.be.server.payment.application.usecase.PaymentDataTransportUseCase;
import kr.hhplus.be.server.payment.application.usecase.RegisterPaymentInfoUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.payment.application.dto.request.RegisterPaymentInfoRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RegisterPaymentInfoService implements RegisterPaymentInfoUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentDataTransportUseCase paymentDataTransportUseCase;

    private final OutboxRepository outboxRepository;


    @Override
    @Transactional
    public PaymentResponse registerPaymentInfo(RegisterPaymentInfoRequest request) {
        // 결제 정보 저장
        Payment payment = paymentRepository.save(Payment.create(request.orderId(), request.totalAmount(), request.paymentMethod()));

        try {
            paymentDataTransportUseCase.send();
        } catch (Exception e) {
            // outbox 테이블에 입력
            outboxRepository.save(Outbox.of(payment.getOrderId(), payment.getPaymentMethod(), LocalDate.now(), payment.getPaymentState()));
        }

        return PaymentResponse.from(payment);
    }



}
