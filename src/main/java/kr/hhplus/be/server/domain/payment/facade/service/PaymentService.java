package kr.hhplus.be.server.domain.payment.facade.service;

import kr.hhplus.be.server.domain.order.controller.request.PaymentMethod;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.facade.payment_method.PaymentStrategy;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.server.domain.payment.facade.service.request.PaymentServiceRequest;
import kr.hhplus.be.server.domain.payment.facade.service.response.PaymentResponse;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicRuntimeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentDataTransportClient paymentDataTransportClient;
    private final Map<PaymentMethod, PaymentStrategy>  paymentMethodStrategyMap;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentDataTransportClient paymentDataTransportClient,
            List<PaymentStrategy> strategies) {
        this.paymentRepository = paymentRepository;
        this.paymentDataTransportClient = paymentDataTransportClient;
        this.paymentMethodStrategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::supportedMethod,
                        strategy -> strategy
                ));

    }

    @Transactional
    public PaymentResponse pay(PaymentServiceRequest request) {
        // 결제 방식 >> request에 결제 방식 받아야 함.
        PaymentStrategy paymentStrategy = paymentMethodStrategyMap.get(request.paymentMethod());
        if (paymentStrategy == null) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.UNSUPPORTED_PAYMENT_METHOD + " : " + request.paymentMethod());
        }
        paymentStrategy.pay(request);

        Payment payment = paymentRepository.save(Payment.register(request));

        paymentDataTransportClient.send();

        return PaymentResponse.from(payment);
    }



}
