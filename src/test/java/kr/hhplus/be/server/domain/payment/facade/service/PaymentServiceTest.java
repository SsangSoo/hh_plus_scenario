package kr.hhplus.be.server.domain.payment.facade.service;

import kr.hhplus.be.server.config.Util;
import kr.hhplus.be.server.domain.order.interfaces.web.request.PaymentMethod;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentState;
import kr.hhplus.be.server.domain.payment.facade.payment_method.BankTransferPayment;
import kr.hhplus.be.server.domain.payment.facade.payment_method.CardPayment;
import kr.hhplus.be.server.domain.payment.facade.payment_method.PaymentStrategy;
import kr.hhplus.be.server.domain.payment.facade.payment_method.PointPayment;
import kr.hhplus.be.server.domain.payment.facade.service.request.PaymentServiceRequest;
import kr.hhplus.be.server.domain.payment.facade.service.response.PaymentResponse;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;
    
    @Mock
    PaymentDataTransportClient paymentDataTransportClient;

    @Mock
    BankTransferPayment bankTransferPayment;
    
    @Mock
    CardPayment cardPayment;

    @Mock
    PointPayment pointPayment;

    PaymentService paymentService;


    @BeforeEach
    void setUp() {
        PaymentStrategy bankTransfer = mock(BankTransferPayment.class);
        when(bankTransfer.supportedMethod()).thenReturn(PaymentMethod.BANK_TRANSFER);

        PaymentStrategy card = mock(CardPayment.class);
        when(card.supportedMethod()).thenReturn(PaymentMethod.CREDIT_CARD);

        PaymentStrategy point = mock(PointPayment.class);
        when(point.supportedMethod()).thenReturn(PaymentMethod.POINT);

        List<PaymentStrategy> strategies = List.of(bankTransfer, card, point);

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
        long orderId = 1L;
        long totalAmount = 4500L;
        PaymentMethod paymentMethod = PaymentMethod.POINT;
        long memberId = 3L;
        PaymentServiceRequest paymentServiceRequest = new PaymentServiceRequest(orderId, totalAmount, paymentMethod, memberId);

        Payment payment = Payment.register(paymentServiceRequest);
        Util.setId(payment, 1L);

        given(paymentRepository.save(any())).willReturn(payment);

        // when
        PaymentResponse paymentResponse = paymentService.pay(paymentServiceRequest);

        // then
        Assertions.assertThat(paymentResponse).isNotNull();
        Assertions.assertThat(paymentResponse.getPaymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE.name());
        Assertions.assertThat(paymentResponse.getId()).isEqualTo(1L);
        Assertions.assertThat(paymentResponse.getTotalAmount()).isEqualTo(4500L);
        Assertions.assertThat(paymentResponse.getOrderId()).isEqualTo(orderId);

        then(paymentDataTransportClient).should(times(1)).send();

    }


    
    
    
}