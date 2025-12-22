package kr.hhplus.be.server.orderproduct.domain.repository;

import kr.hhplus.be.server.orderproduct.domain.model.OrderProduct;

public interface OrderProductRepository {

    OrderProduct save(OrderProduct orderProduct);

}
