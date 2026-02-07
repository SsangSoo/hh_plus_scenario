package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.common.exeption.external.ExternalApiException;
import kr.hhplus.be.server.outbox.domain.event.OutboxInfoEvent;
import kr.hhplus.be.server.payment.application.usecase.PaymentDataTransportUseCase;
import kr.hhplus.be.server.payment.domain.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentDataTransportService implements PaymentDataTransportUseCase {

    private final ApplicationEventPublisher eventPublisher;

    @Async
    @Override
    @Retryable(
            value = {ExternalApiException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Transactional
    public void send(PaymentEvent paymentEvent) {
        log.info("결제완료 메세지 전송");
        eventPublisher.publishEvent(new OutboxInfoEvent(paymentEvent.paymentId(), paymentEvent.orderId()));
    }

    @Recover
    public void recover(ExternalApiException e, Long paymentId, Long orderId) {
        log.error("최종 실패 - paymentId: {}", paymentId);
        // Outbox에 남아있으니 스케줄러가 나중에 재시도
    }


}
