package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import kr.hhplus.be.server.couponhistory.domain.repository.CouponHistoryRepository;
import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.outbox.domain.repository.OutboxRepository;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.application.service.payment_method.PaymentStrategy;
import kr.hhplus.be.server.payment.application.service.payment_method.PointPayment;
import kr.hhplus.be.server.payment.application.usecase.PaymentDataTransportUseCase;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    PaymentDataTransportUseCase paymentDataTransportClient;

    @Mock
    PointPayment pointPayment;

    @Mock
    CouponHistoryRepository couponHistoryRepository;

    @Mock
    CouponRepository couponRepository;

    @Mock
    OutboxRepository outboxRepository;

    @Mock
    StringRedisTemplate stringRedisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    PaymentUseCase paymentService;


    @BeforeEach
    void setUp() {
//        PaymentStrategy bankTransfer = mock(BankTransferPayment.class);
//        when(bankTransfer.supportedMethod()).thenReturn(PaymentMethod.BANK_TRANSFER);
//        PaymentStrategy card = mock(CardPayment.class);
//        when(card.supportedMethod()).thenReturn(PaymentMethod.CREDIT_CARD);

        PaymentStrategy point = pointPayment;
        when(point.supportedMethod()).thenReturn(PaymentMethod.POINT);
        List<PaymentStrategy> strategies = List.of(pointPayment);

        paymentService = new PaymentService(
                paymentRepository,
                paymentDataTransportClient,
                strategies,
                couponRepository,
                couponHistoryRepository,
                outboxRepository,
                stringRedisTemplate
        );
    }



    @Test
    @DisplayName("결제 로직 테스트")
    void paymentTest() {
        // given
        Payment payment = Payment.create(1L, 30000L, PaymentMethod.POINT);
        payment.assignId(1L);
        given(paymentRepository.retrievePayment(any())).willReturn(payment);

        willDoNothing().given(pointPayment).pay(any());

        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(any(), any(), any())).willReturn(true);

        // when
        PaymentResponse response = paymentService.payment(new PaymentServiceRequest(1L, 1L, 1L, null), UUID.randomUUID().toString());

        // then
        assertThat(response.getId()).isEqualTo(payment.getId());
        assertThat(response.getOrderId()).isEqualTo(payment.getOrderId());
        assertThat(response.getTotalAmount()).isEqualTo(payment.getTotalAmount());
        assertThat(response.getPaymentState()).isEqualTo(payment.getPaymentState().toString());
        assertThat(response.getPaymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE.toString());

        // then
        then(pointPayment).should(times(1)).pay(any());
        then(paymentRepository).should(times(1)).changeState(any());
        then(paymentDataTransportClient).should(times(1)).send();
    }



    @Test
    @DisplayName("결제 완료 상태면 예외가 발생한다")
    void ifPaymentStateIsPaymentCompleteThenException() {
        // given
        Payment payment = Payment.create(1L, 30000L, PaymentMethod.POINT);
        payment.assignId(1L);
        payment.changeState(PaymentState.PAYMENT_COMPLETE);
        given(paymentRepository.retrievePayment(any())).willReturn(payment);

        // when // then
        assertThatThrownBy(() -> paymentService.payment(new PaymentServiceRequest(1L, 1L, 1L, null), UUID.randomUUID().toString()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.PAYMENT_COMPLETE.getMessage());
    }

    @Test
    @DisplayName("결제 취소 상태면 예외가 발생한다")
    void ifPaymentStateIsPaymentCancelThenException() {
        // given
        Payment payment = Payment.create(1L, 30000L, PaymentMethod.POINT);
        payment.assignId(1L);
        payment.changeState(PaymentState.PAYMENT_CANCEL);
        given(paymentRepository.retrievePayment(any())).willReturn(payment);

        // when // then
        assertThatThrownBy(() -> paymentService.payment(new PaymentServiceRequest(1L, 1L, 1L, null), UUID.randomUUID().toString()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.PAYMENT_CANCEL.getMessage());
    }


    @Test
    @DisplayName("쿠폰 사용시 쿠폰이 없을 경우, 예외가 발생한다.")
    void whenPaymentIfCouponUsedThenNotFoundCouponExceptionTest() {
        // given
        Payment payment = Payment.create(1L, 30000L, PaymentMethod.POINT);
        payment.assignId(1L);
        given(paymentRepository.retrievePayment(any())).willReturn(payment);

        given(couponHistoryRepository.retrieveCouponHistory(anyLong(), anyLong())).willThrow(new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_COUPON));

        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(any(), any(), any())).willReturn(true);

        // when // then
        assertThatThrownBy(() -> paymentService.payment(new PaymentServiceRequest(1L, 1L, 1L, 1L), UUID.randomUUID().toString()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.NOT_FOUND_COUPON.getMessage());
    }

    @Test
    @DisplayName("쿠폰 사용하려고 할 때, 쿠폰이 이미 사용된 쿠폰일 경우, 예외가 발생한다.")
    void whenPaymentIfAlreadyUsedCouponUsedThenExceptionTest() {
        // given
        Payment payment = Payment.create(1L, 30000L, PaymentMethod.POINT);
        payment.assignId(1L);
        given(paymentRepository.retrievePayment(any())).willReturn(payment);

        CouponHistory couponHistory = CouponHistory.create(1L, 1L);
        couponHistory.use();

        given(couponHistoryRepository.retrieveCouponHistory(anyLong(), anyLong())).willReturn(Optional.of(couponHistory));

        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(any(), any(), any())).willReturn(true);

        // when // then
        assertThatThrownBy(() -> paymentService.payment(new PaymentServiceRequest(1L, 1L, 1L, 1L), UUID.randomUUID().toString()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.ALREADY_USED_THIS_COUPON.getMessage());
    }


    @Test
    @DisplayName("포인트가 부족하면 결제 실패가 된다.")
    void paymentPointExceptionTest() {
        // given
        Payment payment = Payment.create(1L, 30000L, PaymentMethod.POINT);
        payment.assignId(1L);
        given(paymentRepository.retrievePayment(any())).willReturn(payment);

        PaymentServiceRequest PaymentServiceRequest = new PaymentServiceRequest(1L, 1L, 1L, null);

        willThrow(new BusinessLogicRuntimeException(BusinessLogicMessage.POINT_IS_NOT_ENOUGH.getMessage()))
                .given(pointPayment).pay(any());

        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(any(), any(), any())).willReturn(true);

        // when // then
        assertThatThrownBy(() -> paymentService.payment(PaymentServiceRequest, UUID.randomUUID().toString()))
                .hasMessage(BusinessLogicMessage.POINT_IS_NOT_ENOUGH.getMessage())
                .isInstanceOf(BusinessLogicRuntimeException.class);
    }


}