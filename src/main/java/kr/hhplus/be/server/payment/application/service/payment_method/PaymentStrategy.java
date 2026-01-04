package kr.hhplus.be.server.payment.application.service.payment_method;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.payment.application.dto.request.PayServiceRequest;

public interface PaymentStrategy {

    void pay(PayServiceRequest request);

    PaymentMethod supportedMethod();
}
