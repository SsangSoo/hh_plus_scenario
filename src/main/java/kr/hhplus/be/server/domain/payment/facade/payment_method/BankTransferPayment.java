package kr.hhplus.be.server.domain.payment.facade.payment_method;

import kr.hhplus.be.server.domain.order.interfaces.web.request.PaymentMethod;
import kr.hhplus.be.server.domain.payment.facade.service.request.PaymentServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BankTransferPayment implements PaymentStrategy {


    public void pay(PaymentServiceRequest request) {

    }

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.BANK_TRANSFER;
    }
}
