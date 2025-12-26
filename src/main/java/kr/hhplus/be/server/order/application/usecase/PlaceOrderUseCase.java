package kr.hhplus.be.server.order.application.usecase;

import kr.hhplus.be.server.order.application.dto.OrderCommand;
import kr.hhplus.be.server.order.presentation.dto.response.OrderResponse;

public interface PlaceOrderUseCase {

    OrderResponse order(OrderCommand request);
}


