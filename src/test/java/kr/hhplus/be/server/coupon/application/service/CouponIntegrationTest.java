package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.coupon.application.dto.request.IssueCouponServiceRequest;
import kr.hhplus.be.server.coupon.application.dto.request.RegisterCouponServiceRequest;
import kr.hhplus.be.server.coupon.presentation.dto.response.CouponResponse;
import kr.hhplus.be.server.coupon.presentation.dto.response.IssueCouponResponse;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;
import kr.hhplus.be.server.order.application.dto.OrderCommand;
import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.order.presentation.dto.response.OrderResponse;
import kr.hhplus.be.server.orderproduct.application.dto.request.OrderProductServiceRequest;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import kr.hhplus.be.server.payment.presentation.dto.PaymentRequest;
import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import kr.hhplus.be.server.stock.application.dto.request.AddStock;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

class CouponIntegrationTest  extends SpringBootTestSupport {


    @AfterEach
    void tearDown() {
        paymentJpaRepository.deleteAllInBatch();
        memberJpaRepository.deleteAllInBatch();
        pointJpaRepository.deleteAllInBatch();
        pointHistoryJpaRepository.deleteAllInBatch();
        couponJpaRepository.deleteAllInBatch();
        couponHistoryJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("쿠폰 발행 후, 개수 확인 테스트")
    void couponIssueAfterVerifyCouponAmount() {
        // given
        MemberResponse registeredMember = registerMemberUseCase.register(new RegisterMemberCommand("이름", "199010101", "주소"));
        CouponResponse registeredCoupon = registerCouponUseCase.register(new RegisterCouponServiceRequest("123456789012345", LocalDate.now().plusDays(1L), 2, 10));

        CouponResponse retrievedCoupon = retrieveCouponUseCase.retrieve(registeredCoupon.getCouponId());

        assertThat(retrievedCoupon.getAmount()).isEqualTo(2);

        // when
        IssueCouponResponse issueCouponResponse = issueCouponUseCase.issue(new IssueCouponServiceRequest(registeredCoupon.getCouponId(), registeredMember.getId()));

        // then
        retrievedCoupon = retrieveCouponUseCase.retrieve(registeredCoupon.getCouponId());
        assertThat(retrievedCoupon.getAmount()).isEqualTo(1);
    }

    // 회원 생성 -> 쿠폰 생성 -> 쿠폰 발행 -> 상품 등록 -> 재고 증가 -> 포인트 충전 -> 주문 -> 결제
    @Test
    @DisplayName("상품 결제시 쿠폰을 적용하면, 할인 된 금액으로 계산되어야 한다.")
    void IfApplyCouponWhenPayingForProductItShouldBeCalculatedDiscountedAmountTest() {
        // given
        MemberResponse registeredMember = registerMemberUseCase.register(new RegisterMemberCommand("이름", "199010101", "주소"));
        CouponResponse registeredCoupon = registerCouponUseCase.register(new RegisterCouponServiceRequest("123456789012345", LocalDate.now().plusDays(1L), 2, 10));
        IssueCouponResponse issueCouponResponse = issueCouponUseCase.issue(new IssueCouponServiceRequest(registeredCoupon.getCouponId(), registeredMember.getId()));

        assertThat(issueCouponResponse.getCouponUsed()).isFalse();

        ProductResponse registeredProduct = registerProductUseCase.register(new RegisterProductServiceRequest("상품", 10000L));

        StockResponse stockResponse = addStockUseCase.addStock(new AddStock(registeredProduct.getId(), 30L));
        assertThat(stockResponse.getQuantity()).isEqualTo(30L);

        PointResponse pointResponse = chargePointUseCase.charge(new ChargePoint(registeredMember.getId(), 300000L));
        assertThat(pointResponse.getMemberId()).isEqualTo(registeredMember.getId());
        assertThat(pointResponse.getPoint()).isEqualTo(300000L);


        OrderResponse orderResponse = placeOrderUseCase.order(new OrderCommand(registeredMember.getId(), List.of(new OrderProductServiceRequest(registeredProduct.getId(), 20L)), PaymentMethod.POINT));
        assertThat(orderResponse.getMemberId()).isEqualTo(registeredMember.getId());
        assertThat(orderResponse.getTotalAmount()).isEqualTo(200000L);
        assertThat(orderResponse.getPaymentState()).isEqualTo(PaymentState.PENDING.name());

        // when
        PaymentResponse paymentResponse = paymentUseCase.payment(new PaymentServiceRequest(orderResponse.getOrderId(), registeredMember.getId(), orderResponse.getPaymentId(), registeredCoupon.getCouponId()));

        // then
        assertThat(paymentResponse.getTotalAmount()).isEqualTo(180000L);
        assertThat(paymentResponse.getPaymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE.name());

        CouponResponse retrievedCoupon = retrieveCouponUseCase.retrieve(registeredCoupon.getCouponId());
        assertThat(retrievedCoupon.getAmount()).isEqualTo(1);

        PointResponse retrievePoint = retrievePointUseCase.retrieve(registeredMember.getId());
        assertThat(retrievePoint.getPoint()).isEqualTo(120000L);

        StockResponse retrieveStock = retrieveStockUseCase.retrieveStock(registeredProduct.getId());
        assertThat(retrieveStock.getQuantity()).isEqualTo(10L);
    }


    @Test
    @DisplayName("쿠폰 발행은 선착순이다.")
    void couponIssuedFirstComeFirstServedTest() throws InterruptedException  {
        // given
        int couponAmount = 1;
        CouponResponse couponResponse = registerCouponUseCase.register(new RegisterCouponServiceRequest("10% 를 할인해주는 쿠폰", LocalDate.now().plusDays(1L), couponAmount, 10));
        Long couponId = couponResponse.getCouponId();

        List<MemberResponse> list = new ArrayList<>();
        for(int i = 1; i <= 100; i ++) {
            list.add(registerMemberUseCase.register(new RegisterMemberCommand("이름" + i, "19900101", "베이커가 " + i + "번지")));
        }

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            long memberId = i;
            executorService.submit(() -> {
                try {
                    issueCouponUseCase.issue(new IssueCouponServiceRequest(couponId, memberId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(couponAmount);
        assertThat(failCount.get()).isEqualTo(threadCount - couponAmount);
    }
}
