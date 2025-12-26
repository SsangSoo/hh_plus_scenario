package kr.hhplus.be.server.orderproduct.application.usecase;

import kr.hhplus.be.server.orderproduct.application.dto.request.OrderProductServiceRequest;
import kr.hhplus.be.server.orderproduct.application.dto.response.OrderProductResponse;

import java.util.List;

public interface RegisterOrderProductUseCase {
    List<OrderProductResponse> register(List<OrderProductServiceRequest> request, Long orderId);
}
