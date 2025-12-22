package kr.hhplus.be.server.orderproduct.infrastructure.persistence;

import kr.hhplus.be.server.orderproduct.domain.model.OrderProduct;
import kr.hhplus.be.server.orderproduct.domain.repository.OrderProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {

    private final OrderProductJpaRepository jpa;

    @Override
    public OrderProduct save(OrderProduct orderProduct) {
        OrderProductJpaEntity saved = jpa.save(OrderProductJpaEntity.from(orderProduct));
        return saved.toDomain();
    }
}
