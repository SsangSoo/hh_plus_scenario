package kr.hhplus.be.server.payment.presentation;

import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.application.facade.PaymentFacade;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.payment.presentation.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pay")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @PostMapping
    public ResponseEntity<PaymentResponse> pay(
            @RequestHeader("idempotency_key") String idempotencyKey,
            @RequestBody PaymentRequest request) {
        log.info("결제 API 호출 payment request : {}", request);

        PaymentResponse paymentResponse = paymentFacade.payment(request.toServiceRequest(), idempotencyKey);
        return  ResponseEntity.ok(paymentResponse);
    }
}
