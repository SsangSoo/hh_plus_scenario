package kr.hhplus.be.server.orderproduct.application.service;

import kr.hhplus.be.server.orderproduct.domain.model.OrderProduct;
import kr.hhplus.be.server.orderproduct.domain.repository.OrderProductRepository;
import kr.hhplus.be.server.orderproduct.application.dto.request.OrderProductServiceRequest;
import kr.hhplus.be.server.orderproduct.application.dto.response.OrderProductResponse;
import kr.hhplus.be.server.orderproduct.application.usecase.RegisterOrderProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterOrderProductService implements RegisterOrderProductUseCase {

    private final OrderProductRepository orderProductRepository;

    @Transactional
    public OrderProductResponse register(OrderProductServiceRequest request, Long orderId) {

        OrderProduct orderProduct = orderProductRepository.save(OrderProduct.create(request.productId(), orderId, request.quantity()));

        return OrderProductResponse.from(orderProduct);
    }
}

