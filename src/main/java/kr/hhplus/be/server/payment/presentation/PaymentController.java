package kr.hhplus.be.server.payment.presentation;

import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.payment.presentation.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pay")
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    @PostMapping
    public ResponseEntity<PaymentResponse> pay(@RequestBody PaymentRequest request) {
        log.info("결제 API 호출 payment request : {}", request);
        PaymentResponse paymentResponse = paymentUseCase.payment(request.toServiceRequest());
        return  ResponseEntity.ok(paymentResponse);
    }
}
