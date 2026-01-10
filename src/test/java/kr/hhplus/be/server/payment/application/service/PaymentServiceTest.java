package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.outbox.domain.repository.OutboxRepository;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    PaymentDataTransportUseCase paymentDataTransportClient;

    @Mock
    OutboxRepository outboxRepository;

    @Mock
    PaymentTransactionService paymentTransactionService;

    PaymentUseCase paymentService;


    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(
                paymentRepository,
                paymentDataTransportClient,
                outboxRepository,
                paymentTransactionService
        );
    }



    @Test
    @DisplayName("결제 로직 테스트")
    void paymentTest() {
        // given
        Payment payment = Payment.create(1L, 30000L, PaymentMethod.POINT);
        payment.assignId(1L);
        payment.changeState(PaymentState.PAYMENT_COMPLETE);

        PaymentResponse expectedResponse = PaymentResponse.from(payment);

        given(paymentTransactionService.executePayment(any(), any())).willReturn(expectedResponse);
        given(paymentRepository.retrievePayment(any())).willReturn(payment);
        willDoNothing().given(paymentDataTransportClient).send();

        // when
        PaymentResponse response = paymentService.payment(new PaymentServiceRequest(1L, 1L, 1L, null), UUID.randomUUID().toString());

        // then
        assertThat(response.getId()).isEqualTo(payment.getId());
        assertThat(response.getOrderId()).isEqualTo(payment.getOrderId());
        assertThat(response.getTotalAmount()).isEqualTo(payment.getTotalAmount());
        assertThat(response.getPaymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE.toString());

        // then
        then(paymentTransactionService).should(times(1)).executePayment(any(), any());
        then(paymentRepository).should(times(1)).retrievePayment(any());
        then(paymentDataTransportClient).should(times(1)).send();
        then(outboxRepository).should(never()).save(any());
    }



    @Test
    @DisplayName("외부 API 호출 실패 시 Outbox에 저장한다")
    void whenExternalApiFailsThenSaveToOutbox() {
        // given
        Payment payment = Payment.create(1L, 30000L, PaymentMethod.POINT);
        payment.assignId(1L);
        payment.changeState(PaymentState.PAYMENT_COMPLETE);

        PaymentResponse expectedResponse = PaymentResponse.from(payment);

        given(paymentTransactionService.executePayment(any(), any())).willReturn(expectedResponse);
        given(paymentRepository.retrievePayment(any())).willReturn(payment);
        willThrow(new RuntimeException("외부 API 호출 실패")).given(paymentDataTransportClient).send();

        // when
        PaymentResponse response = paymentService.payment(new PaymentServiceRequest(1L, 1L, 1L, null), UUID.randomUUID().toString());

        // then
        assertThat(response.getId()).isEqualTo(payment.getId());
        assertThat(response.getOrderId()).isEqualTo(payment.getOrderId());

        // then
        then(paymentTransactionService).should(times(1)).executePayment(any(), any());
        then(paymentRepository).should(times(1)).retrievePayment(any());
        then(paymentDataTransportClient).should(times(1)).send();
        then(outboxRepository).should(times(1)).save(any());
    }


}