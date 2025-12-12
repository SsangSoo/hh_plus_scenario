package kr.hhplus.be.server.domain.payment.facade.payment_method;

import kr.hhplus.be.server.domain.order.interfaces.web.request.PaymentMethod;
import kr.hhplus.be.server.domain.payment.facade.service.request.PaymentServiceRequest;

public interface PaymentStrategy {
    void pay(PaymentServiceRequest request);
    PaymentMethod supportedMethod();
}
