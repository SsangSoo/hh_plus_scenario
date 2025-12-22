package kr.hhplus.be.server.orderproduct.application.usecase;

import kr.hhplus.be.server.orderproduct.application.dto.request.OrderProductServiceRequest;
import kr.hhplus.be.server.orderproduct.application.dto.response.OrderProductResponse;

public interface RegisterOrderProductUseCase {
    OrderProductResponse register(OrderProductServiceRequest request, Long orderId);
}
