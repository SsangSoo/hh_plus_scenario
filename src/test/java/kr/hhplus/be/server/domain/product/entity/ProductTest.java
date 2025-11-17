package kr.hhplus.be.server.domain.product.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


class ProductTest {

    @Test
    @DisplayName("상품을 생성한다.")
    void productCreateTest() {
        // given // when
        String productName = "고급 볼펜";
        Long productPrice = 1000L;
        Product product = Product.of("고급 볼펜", 1000L);


        // then
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo(productName);
        assertThat(product.getPrice()).isEqualTo(productPrice);

    }


}