package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.outbox.application.usecase.RegisterOutboxUseCase;
import kr.hhplus.be.server.outbox.domain.model.Outbox;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.application.facade.PaymentFacade;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.payment.application.usecase.RetrievePaymentUseCase;
import kr.hhplus.be.server.payment.domain.event.PaymentEvent;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentUseCase paymentUseCase;

    @Mock
    RetrievePaymentUseCase retrievePaymentUseCase;

    @Mock
    RegisterOutboxUseCase registerOutboxUseCase;

    @Mock
    StringRedisTemplate stringRedisTemplate;

    @Mock
    ApplicationEventPublisher eventPublisher;

    PaymentFacade paymentFacade;


    @BeforeEach
    void setUp() {
        paymentFacade = new PaymentFacade(
                paymentUseCase,
                retrievePaymentUseCase,
                registerOutboxUseCase,
                stringRedisTemplate,
                eventPublisher
        );
    }



    @Test
    @DisplayName("결제 로직 테스트")
    void paymentTest() {
        // given
        Payment payment = Payment.create(1L, 30000L);
        payment.assignId(1L);
        payment.changeState(PaymentState.PAYMENT_COMPLETE);

        PaymentResponse expectedResponse = PaymentResponse.from(payment);

        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(any(), any(), any())).willReturn(Boolean.TRUE);

        given(paymentUseCase.payment(any(), any())).willReturn(expectedResponse);
        given(retrievePaymentUseCase.retrievePayment(any())).willReturn(payment);

        Outbox outbox = new Outbox(payment.getId(), payment.getOrderId(), payment.getTotalAmount(), payment.getPaymentState());
        given(registerOutboxUseCase.register(any())).willReturn(outbox);

        // when
        PaymentResponse response = paymentFacade.payment(new PaymentServiceRequest(1L, 1L, 1L, null), UUID.randomUUID().toString());

        // then
        assertThat(response.id()).isEqualTo(payment.getId());
        assertThat(response.orderId()).isEqualTo(payment.getOrderId());
        assertThat(response.totalAmount()).isEqualTo(payment.getTotalAmount());
        assertThat(response.paymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE.toString());

        // then
        then(paymentUseCase).should(times(1)).payment(any(), any());
        then(retrievePaymentUseCase).should(times(1)).retrievePayment(any());
        then(registerOutboxUseCase).should(times(1)).register(any());
        then(eventPublisher).should(times(1)).publishEvent(any(PaymentEvent.class));
    }



}