package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import kr.hhplus.be.server.couponhistory.domain.repository.CouponHistoryRepository;
import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.outbox.domain.repository.OutboxRepository;
import kr.hhplus.be.server.payment.application.dto.request.PayServiceRequest;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.application.service.payment_method.PaymentStrategy;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.payment.application.usecase.PaymentDataTransportUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentService implements PaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentDataTransportUseCase paymentDataTransportUseCase;
    private final Map<PaymentMethod, PaymentStrategy> paymentMethodStrategyMap;
    private final CouponRepository couponRepository;
    private final CouponHistoryRepository couponHistoryRepository;

    private final OutboxRepository outboxRepository;


    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentDataTransportUseCase paymentDataTransportUseCase,
            List<PaymentStrategy> strategies,
            CouponRepository couponRepository,
            CouponHistoryRepository couponHistoryRepository,
            OutboxRepository outboxRepository
    ) {

        this.paymentRepository = paymentRepository;
        this.paymentDataTransportUseCase = paymentDataTransportUseCase;
        this.paymentMethodStrategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::supportedMethod,
                        strategy -> strategy
                ));
        this.couponRepository = couponRepository;
        this.couponHistoryRepository = couponHistoryRepository;
        this.outboxRepository = outboxRepository;
    }


    @Override
    @Transactional
    public PaymentResponse payment(PaymentServiceRequest request) {
        // 결제 정보 조회
        Payment payment = paymentRepository.retrievePayment(request.paymentId());

        // 결제 상태 확인
        checkPaymentState(payment.getPaymentState());

        // 결제 방식 확인
        PaymentStrategy paymentStrategy = paymentMethodStrategyMap.get(payment.getPaymentMethod());

        if (paymentStrategy == null) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.UNSUPPORTED_PAYMENT_METHOD + " : " + payment.getPaymentMethod());
        }

        // 쿠폰 확인
        Long discountApplyAmount = 0L;
        if (Objects.nonNull(request.couponId())) {
            // 쿠폰 발행되었는지 확인
            CouponHistory couponHistory = couponHistoryRepository.retrieveCouponHistory(request.memberId(), request.couponId())
                    .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_COUPON));

            // 쿠폰 사용되었는지 확인
            if(couponHistory.getCouponUsed()) {
                throw new BusinessLogicRuntimeException(BusinessLogicMessage.ALREADY_USED_THIS_COUPON);
            }

            // 쿠폰으로 할인 금액 확인
            Coupon coupon = couponRepository.retrieve(couponHistory.getCouponId());
            discountApplyAmount = coupon.calculateDiscountRate(payment.getTotalAmount()); // 할인 금액 계산
        }

        // 결제 방식에 따른 결제
        paymentStrategy.pay(new PayServiceRequest(payment.getOrderId(), payment.getId(), discountApplyAmount, payment.getTotalAmount(), payment.getPaymentMethod(), request.memberId()));

        // 결제 상태 업데이트
        if(discountApplyAmount > 0L) {
            payment.discountAmount(discountApplyAmount);
        }

        payment.changeState(PaymentState.PAYMENT_COMPLETE);
        paymentRepository.changeState(payment);

        // 데이터 전송
        try {
            paymentDataTransportUseCase.send();
        } catch (Exception e) {
            outboxRepository.paymentComplete(payment.getOrderId());
        }

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

}
