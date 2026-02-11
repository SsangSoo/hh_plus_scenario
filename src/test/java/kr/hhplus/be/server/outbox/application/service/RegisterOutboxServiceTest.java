package kr.hhplus.be.server.outbox.application.service;

import kr.hhplus.be.server.outbox.application.usecase.RegisterOutboxUseCase;
import kr.hhplus.be.server.outbox.domain.model.Outbox;
import kr.hhplus.be.server.outbox.domain.repository.OutboxRepository;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterOutboxServiceTest {

    @Mock
    OutboxRepository outboxRepository;

    RegisterOutboxUseCase registerOutboxUseCase;


    @BeforeEach
    void setUp() {
        registerOutboxUseCase = new RegisterOutboxService(outboxRepository);
    }

    @Test
    @DisplayName("아웃박스 저장 비즈니스 로직 검증")
    void registerOutboxTest() {
        // given
        Outbox outbox = new Outbox(1L, 1L, 1000L, PaymentState.PAYMENT_COMPLETE);

        given(outboxRepository.save(any())).willReturn(outbox);

        // when
        Outbox registered = registerOutboxUseCase.register(outbox);

        // then
        assertThat(registered.orderId()).isEqualTo(outbox.orderId());
        assertThat(registered.paymentId()).isEqualTo(outbox.paymentId());
        assertThat(registered.paymentState()).isEqualTo(outbox.paymentState());
        assertThat(registered.totalAmount()).isEqualTo(outbox.totalAmount());

    }

}