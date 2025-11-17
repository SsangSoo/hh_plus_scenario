package kr.hhplus.be.server.domain.product.service.response;

import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProductResponse {

    private Long id;
    private String name;
    private Long price;
    private Long quantity;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    public static ProductResponse from(Product product, Long quantity) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(quantity)
                .createDate(product.getCreateDate())
                .modifiedDate(product.getModifiedDate())
                .build();
    }

    @Builder
    private ProductResponse(Long id, String name, Long price, Long quantity, LocalDateTime createDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
    }
}
