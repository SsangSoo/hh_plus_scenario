package kr.hhplus.be.server.payment.domain.model;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    @Test
    @DisplayName("결제 생성 테스트, 결제를 포인트로 하면, 결제 완료 상태가 된다.")
    void createPaymentTest() {
        Payment payment = Payment.create(1L, 3000L, PaymentMethod.POINT);

        assertThat(payment.getId()).isNull();
        assertThat(payment.getTotalAmount()).isEqualTo(3000L);
        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE);
    }

    @Test
    @DisplayName("of 으로 테스트")
    void ofTest() {
        Payment payment = Payment.of(1L, 1L,3000L, PaymentState.PAYMENT_COMPLETE);

        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getId()).isEqualTo(1L);
        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE);
    }


    @Test
    @DisplayName("id를 메서드로 추가할 수 있다.")
    void assignIdTest() {
        Payment payment = Payment.create(1L, 3000L, PaymentMethod.POINT);

        assertThat(payment.getId()).isNull();

        payment.assignId(1L);

        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getId()).isEqualTo(1L);
        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE);
    }

    @Test
    @DisplayName("신용카드로 결제할 때, 결제 상태는 PENDING 상태다")
    void payWhenCreditCardMethodTest() {
        Payment payment = Payment.create(1L, 3000L, PaymentMethod.CREDIT_CARD);

        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PENDING);
    }


    @Test
    @DisplayName("무통장 입금으로 결제할 때, 결제 상태는 PENDING 상태다")
    void payWhenBankTransferMethodMethodTest() {
        Payment payment = Payment.create(1L, 3000L, PaymentMethod.BANK_TRANSFER);

        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.PENDING);
    }

}