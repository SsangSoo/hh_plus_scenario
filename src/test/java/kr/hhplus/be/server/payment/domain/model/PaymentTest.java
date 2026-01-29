package kr.hhplus.be.server.payment.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    @Test
    @DisplayName("결제 생성 테스트. 무조건 결제 상태는 PENDING이다.")
    void createPaymentTest() {
        Payment payment = Payment.create(1L, 3000L);

        assertThat(payment.getId()).isNull();
        assertThat(payment.getTotalAmount()).isEqualTo(3000L);
        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PENDING);
    }

    @Test
    @DisplayName("of 으로 테스트")
    void ofTest() {
        Payment payment = Payment.of(1L, 1L, 3000L, PaymentState.PAYMENT_COMPLETE);

        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getId()).isEqualTo(1L);
        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE);
    }


    @Test
    @DisplayName("id를 메서드로 추가할 수 있다.")
    void assignIdTest() {
        Payment payment = Payment.create(1L, 3000L);

        assertThat(payment.getId()).isNull();

        payment.assignId(1L);

        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getId()).isEqualTo(1L);
    }


    @Test
    @DisplayName("결제 상태를 변경할 수 있다.")
    void ableToChangePaymentStateTest() {
        Payment payment = Payment.create(1L, 3000L);

        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PENDING);


        payment.changeState(PaymentState.PAYMENT_COMPLETE);


        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE);
    }

    @Test
    @DisplayName("할인 금액을 적용하면, 총 금액은 바껴야 한다.")
    void applyDiscountAmountThenChangeTotalAmountTest() {
        Payment payment = Payment.create(1L, 3000L);

        assertThat(payment.getTotalAmount()).isEqualTo(3000L);

        payment.discountAmount(1000L);

        assertThat(payment.getTotalAmount()).isEqualTo(2000L);
    }

    @Test
    @DisplayName("결제 완료 메서드 호출시 결제 완료 상태가 된다.")
    void callCompleteMethodAfterPaymentIsCompletedTest() {
        Payment payment = Payment.create(1L, 3000L);

        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PENDING);

        payment.complete();

        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE);
    }
}