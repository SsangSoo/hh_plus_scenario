package kr.hhplus.be.server.payment.application.usecase;

import kr.hhplus.be.server.payment.domain.model.Payment;

public interface ChangePaymentStateUseCase {

    void changeState(Payment payment);

}
