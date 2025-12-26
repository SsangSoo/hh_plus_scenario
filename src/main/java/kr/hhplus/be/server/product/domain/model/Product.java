package kr.hhplus.be.server.product.domain.model;

import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import lombok.Getter;

@Getter
public class Product {

    private Long id;
    private String name;
    private Long price;

    private Product() {
    }

    public static Product create(RegisterProductServiceRequest registerRequest) {
        return new Product(registerRequest.productName(), registerRequest.price());
    }

    public static Product of(Long id, String name, Long price) {
        return new Product(id, name, price);
    }

    private Product(String name, Long price) {
        this.name = name;
        this.price = price;
    }

    private Product(Long id, String name, Long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public void assignId(long id) {
        this.id = id;
    }
}
