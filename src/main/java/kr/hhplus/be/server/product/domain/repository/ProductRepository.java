package kr.hhplus.be.server.product.domain.repository;

import kr.hhplus.be.server.product.domain.model.Product;

public interface ProductRepository {

    Product save(Product product);

    Product findById(Long id);

    void remove(Long id);
}
