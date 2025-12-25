package kr.hhplus.be.server.stock.domain.repository;

import kr.hhplus.be.server.stock.domain.model.Stock;

public interface StockRepository {

    Stock save(Stock stock);

    Stock modify(Stock stock);

    Stock findByProductId(Long productId);

    Stock findByProductIdForUpdate(Long productId);

    Long retrieveStockByProductId(Long productId);

    void remove(Long id);
}
