package kr.hhplus.be.server.product.infrastructure.persistence.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ProductProjection {

    private Long id;
    private String productName;
    private Long price;
    private Long quantity;

    @QueryProjection
    public ProductProjection(Long id, String productName, Long price, Long quantity) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
}
