package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.couponhistory.application.usecase.UseCouponHistoryUseCase;
import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import kr.hhplus.be.server.couponhistory.domain.repository.CouponHistoryRepository;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.point.application.dto.request.UsePoint;
import kr.hhplus.be.server.point.application.usecase.UsePointUseCase;
import kr.hhplus.be.server.product.application.usecase.popular.RegisterPopularProductUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService implements PaymentUseCase {

    private final UsePointUseCase  usePointUseCase;

    private final PaymentRepository paymentRepository;
    private final ChangePaymentStateService changePaymentStateService;

    private final CouponRepository couponRepository;
    private final CouponHistoryRepository couponHistoryRepository;
    private final UseCouponHistoryUseCase useCouponHistoryUseCase;

    private final RegisterPopularProductUseCase registerPopularProductUseCase;


    @Override
    @Transactional
    public PaymentResponse payment(PaymentServiceRequest request, String idempotencyKey) {
        // 결제 정보 조회
        log.info("결제 정보 조회");
        Payment payment = paymentRepository.retrievePayment(request.paymentId());

        // 결제 상태 확인
        checkPaymentState(payment.getPaymentState());

        // 쿠폰 사용시 변경됨.
        Long totalAmount = payment.getTotalAmount();

        totalAmount = useCoupon(request, payment, totalAmount);

        log.info("포인트 결제 시작");
        try {
            // 포인트 결제
            usePointUseCase.use(new UsePoint(request.memberId(), totalAmount));
        } catch (RuntimeException e) { // catch 에서 쿠폰 사용했으면, 다시 사용 가능으로 변경
            CouponHistory couponHistory = couponHistoryRepository.retrieveCouponHistory(request.memberId(), request.couponId())
                    .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_COUPON));
            useCouponHistoryUseCase.couponUseRollback(couponHistory);
            throw e;
        }

        // 결제 상태 업데이트
        payment.complete();
        changePaymentStateService.changeState(payment);

        // Ranking 구현(Async)
        registerPopularProductUseCase.registerPopularProducts(request.orderId(), request.memberId());

        return PaymentResponse.from(payment);
    }

    private void checkPaymentState(PaymentState paymentState) {
        if (paymentState.equals(PaymentState.PAYMENT_COMPLETE)) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.PAYMENT_COMPLETE.getMessage());
        }
        if(paymentState.equals(PaymentState.PAYMENT_CANCEL)) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.PAYMENT_CANCEL.getMessage());
        }
    }


    private Long useCoupon(PaymentServiceRequest request, Payment payment, Long totalAmount) {
        log.info("쿠폰 확인");

        if (Objects.nonNull(request.couponId())) {
            log.info("쿠폰 결제");
            // 발행된 쿠폰 + 사용 가능한 쿠폰 얻어오기
            CouponHistory couponHistory = couponHistoryRepository.retrieveUsableCouponHistory(request.memberId(), request.couponId())
                    .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_USABLE_COUPON));

            // 쿠폰으로 할인 금액 확인
            Coupon coupon = couponRepository.retrieve(couponHistory.getCouponId());
            Long discountApplyAmount = coupon.calculateDiscountRate(payment.getTotalAmount()); // 할인 금액 계산
            payment.discountAmount(discountApplyAmount); // 쿠폰 사용시 총 결제 금액에 할인 금액 반영
            totalAmount -= discountApplyAmount;

            // 쿠폰 사용 반영
            couponHistory.use();
            useCouponHistoryUseCase.couponUse(couponHistory);
        }
        return totalAmount;
    }

//    private LocalDate getThisSaturday() {
//        return LocalDate.now()
//                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
//    }
}
