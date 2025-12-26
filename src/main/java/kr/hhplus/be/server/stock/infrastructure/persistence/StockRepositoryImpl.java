package kr.hhplus.be.server.stock.infrastructure.persistence;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.stock.domain.model.Stock;
import kr.hhplus.be.server.stock.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final StockJpaRepository jpa;

    @Override
    public Stock save(Stock stock) {
        StockJpaEntity saved = jpa.save(StockJpaEntity.from(stock));
        return saved.toDomain();
    }

    @Override
    public Stock modify(Stock stock) {
        StockJpaEntity stockJpaEntity = jpa.findById(stock.getId())
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_STOCK));
        stockJpaEntity.update(stock.getQuantity());
        return stockJpaEntity.toDomain();
    }

    @Override
    public Stock findByProductIdForUpdate(Long productId) {
        StockJpaEntity stockJpaEntity = jpa.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_STOCK));
        return stockJpaEntity.toDomain();
    }

    @Override
    public void remove(Long id) {
        StockJpaEntity stockJpaEntity = jpa.findById(id)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_STOCK));
        stockJpaEntity.remove();
    }


    @Override
    public Stock findByProductId(Long productId) {
        StockJpaEntity stockJpaEntity = jpa.findByProductIdAndRemovedFalse(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_STOCK));
        return stockJpaEntity.toDomain();
    }

    public Long retrieveStockByProductId(Long productId) {
        return jpa.retrieveStockByProductId(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_STOCK));

    }
}
