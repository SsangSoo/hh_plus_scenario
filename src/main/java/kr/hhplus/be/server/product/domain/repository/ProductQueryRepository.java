package kr.hhplus.be.server.product.domain.repository;

import kr.hhplus.be.server.product.infrastructure.persistence.query.ProductProjection;

import java.util.List;
import java.util.Optional;

public interface ProductQueryRepository {

    List<Long> retrieveProductIdWithOrderId(Long orderId);

    List<ProductProjection> retrieveProductsInIds(List<Long> productIdList);

    Optional<ProductProjection> retrieveProductJoinStock(Long productId);
}
