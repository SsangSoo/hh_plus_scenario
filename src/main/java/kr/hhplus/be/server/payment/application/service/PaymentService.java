package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.payment.application.usecase.PaymentDataTransportUseCase;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.application.service.payment_method.PaymentStrategy;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentService implements PaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentDataTransportUseCase paymentDataTransportUseCase;
    private final Map<PaymentMethod, PaymentStrategy>  paymentMethodStrategyMap;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentDataTransportUseCase paymentDataTransportUseCase,
            List<PaymentStrategy> strategies) {
        this.paymentRepository = paymentRepository;
        this.paymentDataTransportUseCase = paymentDataTransportUseCase;
        this.paymentMethodStrategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::supportedMethod,
                        strategy -> strategy
                ));

    }

    @Override
    @Transactional
    public PaymentResponse pay(PaymentServiceRequest request) {
        // 결제 방식 >> request에 결제 방식 받아야 함.
        PaymentStrategy paymentStrategy = paymentMethodStrategyMap.get(request.paymentMethod());
        if (paymentStrategy == null) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.UNSUPPORTED_PAYMENT_METHOD + " : " + request.paymentMethod());
        }
        paymentStrategy.pay(request);

        Payment payment = paymentRepository.save(Payment.create(request.orderId(), request.totalAmount(), request.paymentMethod()));

        paymentDataTransportUseCase.send();

        return PaymentResponse.from(payment);
    }



}
