package kr.hhplus.be.server.stock.domain.model;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import lombok.Getter;

@Getter
public class Stock {

    private Long id;
    private Long productId;
    private Long quantity;

    private Stock() {}

    private Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    private Stock(Long id, Long productId, Long quantity) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public static Stock create(Long productId) {
        Stock stock = new Stock();
        stock.productId = productId;
        stock.quantity = 0L;
        return stock;
    }

    public static Stock of(Long id, Long productId, Long quantity) {
        return new Stock(id, productId, quantity);
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

    public void assignId(long id) {
        this.id = id;
    }
}
