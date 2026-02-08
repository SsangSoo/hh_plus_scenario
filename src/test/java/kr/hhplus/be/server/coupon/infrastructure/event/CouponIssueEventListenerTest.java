package kr.hhplus.be.server.coupon.infrastructure.event;

import kr.hhplus.be.server.coupon.application.usecase.DecreaseCouponUseCase;
import kr.hhplus.be.server.coupon.domain.event.CouponIssueEvent;
import kr.hhplus.be.server.couponhistory.application.usecase.RegisterCouponHistoryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CouponIssueEventListenerTest {

    @Mock
    DecreaseCouponUseCase decreaseCouponUseCase;

    @Mock
    RegisterCouponHistoryUseCase registerCouponHistoryUseCase;

    CouponIssueEventListener couponIssueEventListener;

    @BeforeEach
    void setUp() {
        couponIssueEventListener = new CouponIssueEventListener(
                decreaseCouponUseCase,
                registerCouponHistoryUseCase
        );
    }

    @Test
    @DisplayName("CouponIssueEvent 수신 시, decrease와 register가 호출된다")
    void 이벤트_수신시_decrease_와_register_호출() {
        // given
        Long couponId = 1L;
        Long memberId = 2L;
        CouponIssueEvent event = new CouponIssueEvent(couponId, memberId);

        // when
        couponIssueEventListener.onCouponIssueCompleted(event);

        // then
        then(decreaseCouponUseCase).should(times(1)).decrease(couponId);
        then(registerCouponHistoryUseCase).should(times(1)).register(couponId, memberId);
    }
}
