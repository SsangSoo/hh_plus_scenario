package kr.hhplus.be.server.payment.application.service.payment_method;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.payment.application.dto.request.PayServiceRequest;
import kr.hhplus.be.server.payment.application.dto.request.RegisterPaymentInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardPayment implements PaymentStrategy {

    @Override
    public void pay(PayServiceRequest request) {

    }

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.CREDIT_CARD;
    }
}
