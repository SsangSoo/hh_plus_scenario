package kr.hhplus.be.server.orderproduct.repository;

import kr.hhplus.be.server.orderproduct.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct,Integer> {

}
