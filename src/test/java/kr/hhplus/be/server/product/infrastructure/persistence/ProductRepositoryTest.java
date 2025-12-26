package kr.hhplus.be.server.product.infrastructure.persistence;

import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.product.domain.model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProductRepositoryTest extends SpringBootTestSupport  {

    @AfterEach
    void tearDown() {
        productJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("save 하면 해당 Entity에 id가 생긴다.")
    void saveEntityThenExistsEntityId() {
        // given
        Product product = Product.create(new RegisterProductServiceRequest("상품 이름", 3000L));
        assertThat(product.getId()).isNull();

        // when
        Product savedProduct = productRepository.save(product);

        // then
        assertThat(savedProduct.getId()).isNotNull();
    }

}