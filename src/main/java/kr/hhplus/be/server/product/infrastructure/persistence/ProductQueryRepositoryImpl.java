package kr.hhplus.be.server.product.infrastructure.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.product.domain.repository.ProductQueryRepository;
import kr.hhplus.be.server.product.infrastructure.persistence.query.ProductProjection;
import kr.hhplus.be.server.product.infrastructure.persistence.query.QProductProjection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.server.orderproduct.infrastructure.persistence.QOrderProductJpaEntity.*;
import static kr.hhplus.be.server.product.infrastructure.persistence.QProductJpaEntity.*;
import static kr.hhplus.be.server.stock.infrastructure.persistence.QStockJpaEntity.*;

@Repository
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    private final JPAQueryFactory query;

    public ProductQueryRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }


    @Override
    public List<Long> retrieveProductIdWithOrderId(Long orderId) {
        return query
                .select(orderProductJpaEntity.productId)
                .from(orderProductJpaEntity)
                .where(orderProductJpaEntity.orderId.eq(orderId))
                .fetch()
                .stream()
                .distinct()
                .toList();


    }

    @Override
    public List<ProductProjection> retrieveProductsInIds(List<Long> productIdList) {
        return query
                .select(new QProductProjection(
                        productJpaEntity.id,
                        productJpaEntity.name,
                        productJpaEntity.price,
                        stockJpaEntity.quantity))
                .from(productJpaEntity)
                .where(productJpaEntity.id.in(productIdList))
                .join(stockJpaEntity)
                .on(stockJpaEntity.id.eq(productJpaEntity.id))
                .orderBy(productJpaEntity.id.asc())
                .fetch();
    }

    @Override
    public Optional<ProductProjection> retrieveProductJoinStock(Long productId) {
        ProductProjection productProjection = query
                .select(new QProductProjection(
                        productJpaEntity.id,
                        productJpaEntity.name,
                        productJpaEntity.price,
                        stockJpaEntity.quantity))
                .from(productJpaEntity)
                .where(productJpaEntity.id.eq(productId))
                .join(stockJpaEntity)
                .on(stockJpaEntity.id.eq(productJpaEntity.id))
                .orderBy(productJpaEntity.id.asc())
                .fetchOne();

        return Optional.ofNullable(productProjection);
    }

}
