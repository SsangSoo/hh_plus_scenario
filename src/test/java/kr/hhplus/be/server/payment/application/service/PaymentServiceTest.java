package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.payment.application.usecase.PaymentDataTransportUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import kr.hhplus.be.server.payment.application.service.payment_method.PaymentStrategy;
import kr.hhplus.be.server.payment.application.service.payment_method.PointPayment;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    PaymentDataTransportUseCase paymentDataTransportClient;

    @Mock
    PointPayment pointPayment;

    PaymentService paymentService;


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
                strategies
        );

    }

    @Test
    @DisplayName("포인트 결제 로직 테스트")
    void paymentPointTest() {
        // given
        long memberId = 3L;
        PaymentServiceRequest paymentServiceRequest = new PaymentServiceRequest(1L, 4500L, PaymentMethod.POINT, memberId);

        Payment payment = Payment.create(1L, 4500L, PaymentMethod.POINT);
        payment.assignId(1L);

        given(paymentRepository.save(any())).willReturn(payment);

        // when
        PaymentResponse paymentResponse = paymentService.pay(paymentServiceRequest);

        // then
        Assertions.assertThat(paymentResponse).isNotNull();
        Assertions.assertThat(paymentResponse.getPaymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE.name());
        Assertions.assertThat(paymentResponse.getId()).isEqualTo(1L);
        Assertions.assertThat(paymentResponse.getTotalAmount()).isEqualTo(4500L);
        Assertions.assertThat(paymentResponse.getOrderId()).isEqualTo(paymentServiceRequest.orderId());

        then(paymentDataTransportClient).should(times(1)).send();
    }

    @Test
    @DisplayName("포인트가 부족하면 결제 실패가 된다.")
    void paymentPointExceptionTest() {
        // given
        PaymentServiceRequest paymentServiceRequest = new PaymentServiceRequest(1L, 4500L, PaymentMethod.POINT, 3L);

        willThrow(new BusinessLogicRuntimeException(BusinessLogicMessage.POINT_IS_NOT_ENOUGH.getMessage()))
                .given(pointPayment).pay(any());

        // when // then
        Assertions.assertThatThrownBy(() -> paymentService.pay(paymentServiceRequest))
                .hasMessage(BusinessLogicMessage.POINT_IS_NOT_ENOUGH.getMessage())
                .isInstanceOf(BusinessLogicRuntimeException.class);
    }
}