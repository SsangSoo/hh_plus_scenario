package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.payment.application.usecase.PaymentDataTransportUseCase;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataTransportService implements PaymentDataTransportUseCase {

    @Async
    @Override
    public void send() {

    }

    @Async
    @Override
    public void send(String exception) {
        throw new IllegalStateException("데이터 전송 중 예외가 발행했습니다.");
    }
}
