package kr.hhplus.be.server.outbox.application.service;

import kr.hhplus.be.server.outbox.application.usecase.RegisterOutboxUseCase;
import kr.hhplus.be.server.outbox.application.usecase.RetrieveOutboxUseCase;
import kr.hhplus.be.server.outbox.domain.model.Outbox;
import kr.hhplus.be.server.outbox.domain.repository.OutboxRepository;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RetrieveOutboxServiceTest {

    @Mock
    OutboxRepository outboxRepository;

    RetrieveOutboxUseCase retrieveOutboxUseCase;


    @BeforeEach
    void setUp() {
        retrieveOutboxUseCase = new RetrieveOutboxService(outboxRepository);
    }

    @Test
    @DisplayName("아웃박스 조회 비즈니스 로직 검증")
    void registerOutboxTest() {
        // given
        Outbox outbox = new Outbox(1L, 1L, 1000L, PaymentState.PAYMENT_COMPLETE);

        given(outboxRepository.retrieve(any(), any())).willReturn(outbox);

        // when
        Outbox retrieved = retrieveOutboxUseCase.retrieve(1L, 1L);

        // then
        assertThat(retrieved.orderId()).isEqualTo(outbox.orderId());
        assertThat(retrieved.paymentId()).isEqualTo(outbox.paymentId());
        assertThat(retrieved.paymentState()).isEqualTo(outbox.paymentState());
        assertThat(retrieved.totalAmount()).isEqualTo(outbox.totalAmount());

    }


}