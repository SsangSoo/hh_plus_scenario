package kr.hhplus.be.server.product.infrastructure.persistence;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpa;


    @Override
    public Product save(Product product) {
        ProductJpaEntity saved = jpa.save(ProductJpaEntity.from(product));
        return saved.toDomain();
    }

    @Override
    public Product findById(Long id) {
        ProductJpaEntity productJpaEntity = jpa.findByIdAndRemovedFalse(id)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_PRODUCT));
        return productJpaEntity.toDomain();
    }

    @Override
    public void remove(Long id) {
        ProductJpaEntity productJpaEntity = jpa.findByIdAndRemovedFalse(id)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_STOCK));
        productJpaEntity.remove();
    }

    @Override
    public List<Product> findByIds(List<Long> productIdList) {
        List<ProductJpaEntity> productJpaEntities = jpa.findIdsByIdIn(productIdList);
        if(productIdList.size() != productJpaEntities.size()) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_SOME_PRODUCT);
        }
        return productJpaEntities.stream()
                .map(ProductJpaEntity::toDomain)
                .toList();
    }
}
