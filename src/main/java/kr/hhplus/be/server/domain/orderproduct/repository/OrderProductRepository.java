package kr.hhplus.be.server.domain.orderproduct.repository;

import kr.hhplus.be.server.domain.orderproduct.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct,Integer> {

}
