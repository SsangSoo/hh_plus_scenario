package kr.hhplus.be.server.payment.application.facade;



import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.outbox.application.usecase.RegisterOutboxUseCase;
import kr.hhplus.be.server.outbox.domain.model.Outbox;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.payment.application.usecase.RetrievePaymentUseCase;
import kr.hhplus.be.server.payment.domain.event.PaymentEvent;
import kr.hhplus.be.server.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentUseCase paymentUseCase;
    private final RetrievePaymentUseCase retrievePaymentUseCase;

    private final RegisterOutboxUseCase registerOutboxUseCase;

    private final StringRedisTemplate stringRedisTemplate;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PaymentResponse payment(PaymentServiceRequest request, String idempotencyKey) {
        // 결제 상태 확인이 끝나면, 처리해야 할 결제이므로, 레디스에 중복 요청 방지
        if(verifyDuplicatePayment(idempotencyKey)) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.ALREADY_PROCESSING_THIS_PAYMENT);
        }
        // 1. 트랜잭션 내 처리 (DB 작업)
        PaymentResponse response = paymentUseCase.payment(request, idempotencyKey);

        // 2. 트랜잭션 외 처리 (외부 API 호출)
        Payment payment = retrievePaymentUseCase.retrievePayment(response.id());

        //todo
        // 같은 트랜잭션 내에서 outbox 저장
        registerOutboxUseCase.register(new Outbox(payment.getId(), payment.getOrderId(), payment.getTotalAmount(), payment.getPaymentState()));

        // 외부 API 전송 -> PaymentEvent 로 변경
        eventPublisher.publishEvent(new PaymentEvent(payment.getId(), payment.getOrderId()));

        return PaymentResponse.from(payment);
    }

    private Boolean verifyDuplicatePayment(String idempotencyKey) {
        Boolean processing = stringRedisTemplate.opsForValue().setIfAbsent(
                "idempotencyKey:" + idempotencyKey,
                "PROCESSING",
                Duration.ofMinutes(30)
        );
        return !processing;
    }


}