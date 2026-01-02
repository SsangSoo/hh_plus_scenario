package kr.hhplus.be.server.payment.application.usecase;

import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.presentation.dto.PaymentRequest;

public interface PaymentUseCase {

    PaymentResponse payment(PaymentRequest request);

}
