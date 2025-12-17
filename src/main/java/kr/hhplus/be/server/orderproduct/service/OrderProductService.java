package kr.hhplus.be.server.orderproduct.service;

import kr.hhplus.be.server.orderproduct.entity.OrderProduct;
import kr.hhplus.be.server.orderproduct.repository.OrderProductRepository;
import kr.hhplus.be.server.orderproduct.service.request.OrderProductServiceRequest;
import kr.hhplus.be.server.orderproduct.service.response.OrderProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderProductService {
    private final OrderProductRepository orderProductRepository;

    @Transactional
    public OrderProductResponse register(OrderProductServiceRequest request, Long orderId) {

        OrderProduct orderProduct = orderProductRepository.save(OrderProduct.register(request, orderId));

        return OrderProductResponse.from(orderProduct);
    }
}

