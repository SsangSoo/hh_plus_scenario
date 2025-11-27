package kr.hhplus.be.server.domain.stock.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("select p.id from Product p where p.id = :productId and p.deleted = false")
    Optional<Long> findProductIdByProductId(Long productId);

    Optional<Stock> findByProductIdAndDeletedFalse(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.id = :id and s.deleted = false")
    Stock findbyIdForUpdate(Long id);
}
