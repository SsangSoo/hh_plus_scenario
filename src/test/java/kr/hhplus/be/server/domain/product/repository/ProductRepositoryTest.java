package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.service.request.RegisterProductServiceRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProductRepositoryTest extends SpringBootTestSupport  {

    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("save 하면 해당 Entity에 id가 생긴다.")
    void saveEntityThenExistsEntityId() {
        // given
        Product product = Product.register(new RegisterProductServiceRequest("상품 이름", 3000L));
        Assertions.assertThat(product.getId()).isNull();

        // when
        Product savedProduct = productRepository.save(product);

        // then
        Assertions.assertThat(product.getId()).isNotNull();
        Assertions.assertThat(product.getId()).isEqualTo(savedProduct.getId());
    }

}