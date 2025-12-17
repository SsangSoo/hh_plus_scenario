package kr.hhplus.be.server.product.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.hhplus.be.server.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProductResponse {

    private Long id;
    private String productName;
    private Long price;
    private Long quantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modifiedDate;

    public static ProductResponse from(Product product, Long quantity) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .quantity(quantity)
                .createDate(product.getCreatedDate().withNano(0))
                .modifiedDate(product.getModifiedDate().withNano(0))
                .build();
    }

    @Builder
    private ProductResponse(Long id, String productName, Long price, Long quantity, LocalDateTime createDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
    }
}
