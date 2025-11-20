package kr.hhplus.be.server.domain.stock.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "STOCK")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    public static Stock register(Long productId) {
        Stock stock = new Stock();

        stock.productId = productId;

        stock.quantity = 0L;
        stock.createdDate = LocalDateTime.now();
        stock.modifiedDate = stock.createdDate;
        stock.deleted = false;
        return stock;
    }

    public void delete() {
        deleted = true;
    }

}

