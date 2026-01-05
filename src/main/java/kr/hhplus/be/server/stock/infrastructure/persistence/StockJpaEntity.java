package kr.hhplus.be.server.stock.infrastructure.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseEntity;
import kr.hhplus.be.server.stock.domain.model.Stock;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "STOCK")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockJpaEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;



    public static StockJpaEntity from(Stock stock) {
        StockJpaEntity stockJpaEntity = new StockJpaEntity();

        stockJpaEntity.productId = stock.getProductId();
        stockJpaEntity.quantity = stock.getQuantity();

        stockJpaEntity.createdDate = LocalDateTime.now();
        stockJpaEntity.modifiedDate = stockJpaEntity.createdDate;
        stockJpaEntity.removed = false;
        return stockJpaEntity;
    }

    public void modify(Long quantity) {
        this.quantity = quantity;
    }

    public Stock toDomain() {
        return Stock.of(
                id,
                productId,
                quantity
        );
    }
}

