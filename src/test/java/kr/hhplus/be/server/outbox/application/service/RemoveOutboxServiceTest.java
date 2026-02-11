package kr.hhplus.be.server.outbox.application.service;

import kr.hhplus.be.server.outbox.application.usecase.RegisterOutboxUseCase;
import kr.hhplus.be.server.outbox.application.usecase.RemoveOutboxUseCase;
import kr.hhplus.be.server.outbox.application.usecase.RetrieveOutboxUseCase;
import kr.hhplus.be.server.outbox.domain.model.Outbox;
import kr.hhplus.be.server.outbox.domain.repository.OutboxRepository;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RemoveOutboxServiceTest {

    @Mock
    OutboxRepository outboxRepository;

    RemoveOutboxUseCase removeOutboxUseCase;



    @BeforeEach
    void setUp() {
        removeOutboxUseCase = new RemoveOutboxService(outboxRepository);
    }

    @Test
    @DisplayName("아웃박스 삭제 비즈니스 로직 검증")
    void removeOutbox() {
        // given
        willDoNothing().given(outboxRepository).remove(any(), any());

        // when
        removeOutboxUseCase.remove(1L, 1L);

        // then
        then(outboxRepository).should(times(1)).remove(1L, 1L);
    }


}