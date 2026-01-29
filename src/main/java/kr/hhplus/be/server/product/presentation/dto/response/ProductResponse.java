package kr.hhplus.be.server.product.presentation.dto.response;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.infrastructure.persistence.query.ProductProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponse {

    private Long id;
    private String productName;
    private Long price;
    private Long quantity;

    public static ProductResponse from(Product product, Long quantity) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .quantity(quantity)
                .build();
    }


    public static ProductResponse from(ProductProjection productProjection) {
        return ProductResponse.builder()
                .id(productProjection.getId())
                .productName(productProjection.getProductName())
                .price(productProjection.getPrice())
                .quantity(productProjection.getQuantity())
                .build();
    }

    @Builder
    private ProductResponse(Long id, String productName, Long price, Long quantity) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
}
