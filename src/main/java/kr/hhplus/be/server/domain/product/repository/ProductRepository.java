package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    @Query(
            value = "SELECT quantity " +
                    "FROM STOCK " +
                    "WHERE product_id = :productId",
            nativeQuery = true
    )
    Optional<Long> retrieveStockByProductId(Long productId);
}
