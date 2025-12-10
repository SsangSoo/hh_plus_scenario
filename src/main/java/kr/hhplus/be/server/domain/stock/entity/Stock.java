package kr.hhplus.be.server.domain.stock.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.base.BaseEntity;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicRuntimeException;
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

        stock.createdDate = LocalDateTime.now();
        stock.modifiedDate = stock.createdDate;
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
        validationStock(quantity);
        this.quantity -= quantity;
    }

    /**
     * 재고 차감 가능 확인
     * @param quantity
     * @return
     */
    public void validationStock(Long quantity) {
        if(this.quantity < quantity) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.STOCK_IS_NOT_ENOUGH);
        }
    }

    public void delete() {
        deleted = true;
    }

}

