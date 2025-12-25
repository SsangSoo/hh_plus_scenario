package kr.hhplus.be.server.orderproduct.infrastructure.persistence;

import kr.hhplus.be.server.orderproduct.domain.model.OrderProduct;
import kr.hhplus.be.server.orderproduct.domain.repository.OrderProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {

    private final OrderProductJpaRepository jpa;

    @Override
    public OrderProduct save(OrderProduct orderProduct) {
        OrderProductJpaEntity saved = jpa.save(OrderProductJpaEntity.from(orderProduct));
        return saved.toDomain();
    }

    @Override
    public List<OrderProduct> saveAll(List<OrderProduct> orderProducts) {
        List<OrderProductJpaEntity> list = orderProducts.stream()
                .map(OrderProductJpaEntity::from)
                .toList();
        List<OrderProductJpaEntity> savedAll = jpa.saveAll(list);

        return savedAll.stream()
                .map(OrderProductJpaEntity::toDomain)
                .toList();

    }

    @Override
    public List<OrderProduct> findByOrderId(Long orderId) {
        List<OrderProductJpaEntity> listByOrderId = jpa.findByOrderId(orderId);
        return listByOrderId.stream()
                .map(OrderProductJpaEntity::toDomain)
                .toList();

    }
}
