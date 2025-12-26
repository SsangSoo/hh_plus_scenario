package kr.hhplus.be.server.orderproduct.domain.repository;

import kr.hhplus.be.server.orderproduct.domain.model.OrderProduct;

import java.util.List;

public interface OrderProductRepository {

    OrderProduct save(OrderProduct orderProduct);

    List<OrderProduct> saveAll(List<OrderProduct> orderProducts);

    List<OrderProduct> findByOrderId(Long orderId);
}
