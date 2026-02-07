package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.payment.application.usecase.RegisterPaymentUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import kr.hhplus.be.server.payment.application.dto.request.RegisterPaymentInfoRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterPaymentInfoServiceTest {

    @Mock
    PaymentRepository paymentRepository;

    RegisterPaymentUseCase registerPaymentService;

    @BeforeEach
    void setUp() {
        registerPaymentService = new RegisterPaymentService(paymentRepository);
    }

    @Test
    @DisplayName("결제 정보 입력 로직 테스트")
    void paymentPointTest() {
        // given
        long memberId = 3L;
        RegisterPaymentInfoRequest paymentServiceRequest = new RegisterPaymentInfoRequest(1L, 4500L, memberId);

        Payment payment = Payment.create(1L, 4500L);
        payment.assignId(1L);

        given(paymentRepository.save(any())).willReturn(payment);

        // when
        PaymentResponse paymentResponse = registerPaymentService.registerPaymentInfo(paymentServiceRequest);

        // then
        Assertions.assertThat(paymentResponse).isNotNull();
        Assertions.assertThat(paymentResponse.paymentState()).isEqualTo(PaymentState.PENDING.name());
        Assertions.assertThat(paymentResponse.id()).isEqualTo(1L);
        Assertions.assertThat(paymentResponse.totalAmount()).isEqualTo(4500L);
        Assertions.assertThat(paymentResponse.orderId()).isEqualTo(paymentServiceRequest.orderId());
    }


}