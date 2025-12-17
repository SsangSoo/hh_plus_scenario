package kr.hhplus.be.server.payment.facade.payment_method;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.payment.facade.service.request.PaymentServiceRequest;

public interface PaymentStrategy {
    void pay(PaymentServiceRequest request);
    PaymentMethod supportedMethod();
}
