package kr.hhplus.be.server.stock.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockJpaRepository extends JpaRepository<StockJpaEntity, Long> {

    Optional<StockJpaEntity> findByProductIdAndRemovedFalse(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StockJpaEntity s where s.productId = :productId and s.removed = false and s.quantity >= :quantity")
    Optional<StockJpaEntity> findByProductIdForDeduct(Long productId, Long quantity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StockJpaEntity s where s.productId = :productId and s.removed = false")
    Optional<StockJpaEntity> findByProductIdForUpdate(Long productId);


    @Query(
            value = "SELECT quantity " +
                    "FROM STOCK " +
                    "WHERE product_id = :productId AND " +
                    "removed = false"
            ,
            nativeQuery = true
    )
    Optional<Long> retrieveStockByProductId(Long productId);

}
