package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    public void delete() {
        deleted = true;
    }

    public static Product of(String name, long price) {
        return new Product(name, price);
    }

    private Product(String name, long price) {
        this(name, price, LocalDateTime.now(), LocalDateTime.now());
    }

    private Product(String name, Long price, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.name = name;
        this.price = price;
        this.createDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.deleted = false;
    }

}