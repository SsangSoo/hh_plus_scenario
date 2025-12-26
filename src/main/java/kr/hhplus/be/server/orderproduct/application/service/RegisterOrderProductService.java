package kr.hhplus.be.server.orderproduct.application.service;

import kr.hhplus.be.server.orderproduct.domain.model.OrderProduct;
import kr.hhplus.be.server.orderproduct.domain.repository.OrderProductRepository;
import kr.hhplus.be.server.orderproduct.application.dto.request.OrderProductServiceRequest;
import kr.hhplus.be.server.orderproduct.application.dto.response.OrderProductResponse;
import kr.hhplus.be.server.orderproduct.application.usecase.RegisterOrderProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisterOrderProductService implements RegisterOrderProductUseCase {

    private final OrderProductRepository orderProductRepository;

    @Transactional
    public List<OrderProductResponse> register(List<OrderProductServiceRequest> requests, Long orderId) {

        List<OrderProduct> orderProducts = new ArrayList<>();
        for(OrderProductServiceRequest orderProductServiceRequest : requests){
            orderProducts.add(OrderProduct.create(orderProductServiceRequest.productId(), orderId, orderProductServiceRequest.quantity()));
        }

        List<OrderProduct> savedOrderProducts = orderProductRepository.saveAll(orderProducts);

        return savedOrderProducts.stream()
                .map(OrderProductResponse::from)
                .toList();
    }
}

