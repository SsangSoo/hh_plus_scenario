package kr.hhplus.be.server.payment.application.usecase;

import kr.hhplus.be.server.payment.domain.event.PaymentEvent;

public interface PaymentDataTransportUseCase {
    void send(PaymentEvent paymentEvent);
}
