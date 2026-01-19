package kr.hhplus.be.server.payment.application.usecase;

import kr.hhplus.be.server.payment.domain.model.Payment;

public interface RetrievePaymentUseCase {

    Payment retrievePayment(Long paymentId);

}
