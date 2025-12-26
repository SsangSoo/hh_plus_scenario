package kr.hhplus.be.server.product.infrastructure.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseEntity;
import kr.hhplus.be.server.product.domain.model.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductJpaEntity extends BaseEntity {

    @Id @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    public static ProductJpaEntity from(Product product) {
        ProductJpaEntity productJpaEntity = new ProductJpaEntity();

        productJpaEntity.name =  product.getName();
        productJpaEntity.price = product.getPrice();

        productJpaEntity.createdDate = LocalDateTime.now();
        productJpaEntity.modifiedDate = productJpaEntity.createdDate;
        productJpaEntity.removed = false;

        return productJpaEntity;
    }


    public Product toDomain() {
        return Product.of(
                id,
                name,
                price
        );
    }

}