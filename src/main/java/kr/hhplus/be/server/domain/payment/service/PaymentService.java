package kr.hhplus.be.server.domain.payment.service;

import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.server.domain.payment.service.request.PaymentServiceRequest;
import kr.hhplus.be.server.domain.payment.service.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentDataTransportClient paymentDataTransportClient;

    @Transactional
    public PaymentResponse register(PaymentServiceRequest request) {
        Payment payment = Payment.register(request);

        paymentRepository.save(payment);

        paymentDataTransportClient.send();

        return PaymentResponse.from(payment);
    }
}
