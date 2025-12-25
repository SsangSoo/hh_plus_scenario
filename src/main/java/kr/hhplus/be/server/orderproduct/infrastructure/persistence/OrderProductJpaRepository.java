package kr.hhplus.be.server.orderproduct.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductJpaRepository extends JpaRepository<OrderProductJpaEntity,Integer> {

    List<OrderProductJpaEntity> findByOrderId(Long orderId);
}
