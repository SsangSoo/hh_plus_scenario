package kr.hhplus.be.server.order.infrastructure.persistence;

import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryJpaImpl implements OrderRepository {

    private final OrderJpaRepository jpa;

    @Override
    public Order save(Order order) {

        OrderJpaEntity orderEntity = OrderJpaEntity.from(order);
        OrderJpaEntity saved = jpa.save(orderEntity);



        return saved.toDomain();
    }
}
