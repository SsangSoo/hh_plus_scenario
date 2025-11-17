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
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .createDate(product.getCreatedDate())
                .modifiedDate(product.getModifiedDate())
                .build();
    }

    @Builder
    private ProductResponse(Long id, String name, Long price, LocalDateTime createDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
    }
}
