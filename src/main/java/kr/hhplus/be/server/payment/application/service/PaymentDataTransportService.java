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
}
