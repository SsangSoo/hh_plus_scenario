package kr.hhplus.be.server.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseTimeEntity;
import kr.hhplus.be.server.product.service.request.RegisterProductServiceRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;


    public static Product register(RegisterProductServiceRequest request) {
        return register(request.productName(), request.price());
    }

    private static Product register(String name, long price) {
        Product product = new Product();

        product.name =  name;
        product.price = price;

        product.createdDate = LocalDateTime.now();
        product.modifiedDate = product.createdDate;
        product.deleted = false;

        return product;
    }

    public void delete() {
        deleted = true;
    }


}