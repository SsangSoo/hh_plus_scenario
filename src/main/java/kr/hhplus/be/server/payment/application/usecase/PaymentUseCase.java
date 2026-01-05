package kr.hhplus.be.server.payment.application.usecase;

import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;

public interface PaymentUseCase {

    PaymentResponse payment(PaymentServiceRequest request, String idempotencyKey);

}
