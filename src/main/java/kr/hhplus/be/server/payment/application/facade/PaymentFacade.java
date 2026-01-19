package kr.hhplus.be.server.payment.application.facade;

import kr.hhplus.be.server.outbox.domain.model.Outbox;
import kr.hhplus.be.server.outbox.domain.repository.OutboxRepository;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.application.usecase.PaymentDataTransportUseCase;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.payment.application.usecase.RetrievePaymentUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentUseCase paymentUseCase;
    private final RetrievePaymentUseCase retrievePaymentUseCase;
    private final PaymentDataTransportUseCase paymentDataTransportUseCase;
    private final OutboxRepository outboxRepository;

    public PaymentResponse payment(PaymentServiceRequest request, String idempotencyKey) {
        // 1. 트랜잭션 내 처리 (DB 작업)
        PaymentResponse response = paymentUseCase.payment(request, idempotencyKey);

        // 2. 트랜잭션 외 처리 (외부 API 호출)
        Payment payment = retrievePaymentUseCase.retrievePayment(request.paymentId());
        try {
            paymentDataTransportUseCase.send();
            log.info("외부 API 호출 성공 - paymentId: {}", payment.getId());
        } catch (Exception e) {
            log.error("외부 API 호출 실패 - paymentId: {}, 에러: {}", payment.getId(), e.getMessage());
            // 실패 시에만 Outbox 저장
            outboxRepository.save(Outbox.of(payment.getId(), payment.getOrderId(), payment.getTotalAmount(), payment.getPaymentState()));
            log.info("Outbox에 저장 완료 - paymentId: {}", payment.getId());
        }
        return response;
    }

}