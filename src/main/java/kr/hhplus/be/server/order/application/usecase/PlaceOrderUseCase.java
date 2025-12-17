package kr.hhplus.be.server.order.application.usecase;

import kr.hhplus.be.server.order.application.dto.OrderCommand;
import kr.hhplus.be.server.order.application.dto.OrderResult;

public interface PlaceOrderUseCase {

    OrderResult order(OrderCommand request);
}


