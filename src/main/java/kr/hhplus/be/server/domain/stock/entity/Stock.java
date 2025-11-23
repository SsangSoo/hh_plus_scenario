package kr.hhplus.be.server.domain.stock.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "STOCK")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    public static Stock register(Long productId) {
        Stock stock = new Stock();

        stock.productId = productId;

        stock.quantity = 0L;
        stock.deleted = false;
        return stock;
    }

    /**
     * 추후 단위 혹은 정해진 값에 따라 변경 가능
     * @param quantity
     */
    public void addStock(Long quantity) {
        this.quantity += quantity;
    }

    /**
     * 재고 차감
     * @param quantity
     */
    public void deductedStock(Long quantity) {
        if(validateStock(quantity)) {
            this.quantity -= quantity;
        }
    }

    /**
     * 재고 차감 가능 확인
     * @param quantity
     * @return
     */
    public boolean validateStock(Long quantity) {
        if (this.quantity < quantity) {
            throw new IllegalArgumentException("현재 재고보다 차감하려는 재고가 많습니다.");
        }
        return this.quantity >= quantity;
    }

    public void delete() {
        deleted = true;
    }

}

