package kr.hhplus.be.server.payment.application.usecase;

import kr.hhplus.be.server.payment.application.dto.request.RegisterPaymentInfoRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;

public interface RegisterPaymentUseCase {

    PaymentResponse registerPaymentInfo(RegisterPaymentInfoRequest request);

}
