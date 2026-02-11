package kr.hhplus.be.server.coupon.infrastructure.event;

import kr.hhplus.be.server.coupon.application.usecase.DecreaseCouponUseCase;
import kr.hhplus.be.server.coupon.domain.event.CouponIssueEvent;
import kr.hhplus.be.server.couponhistory.application.usecase.RegisterCouponHistoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponIssueEventListener {

    private final DecreaseCouponUseCase decreaseCouponUseCase;
    private final RegisterCouponHistoryUseCase registerCouponHistoryUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void onCouponIssueCompleted(CouponIssueEvent event) {
        decreaseCouponUseCase.decrease(event.couponId());
        registerCouponHistoryUseCase.register(event.couponId(), event.memberId());
    }


}
