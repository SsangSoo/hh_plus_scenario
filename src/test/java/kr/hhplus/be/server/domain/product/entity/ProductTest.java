package kr.hhplus.be.server.domain.product.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


class ProductTest {

    @Test
    @DisplayName("상품을 생성한다.")
    void productCreateTest() {
        // given : 상품 생성시 필요한 값 설정
        String productName = "고급 볼펜";
        Long productPrice = 1000L;

        // when : 상품 생성
        Product product = Product.of(productName, productPrice);

        // then : 상품의 생성 여부 / 생성 시 주입된 값들 검증
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo(productName);
        assertThat(product.getPrice()).isEqualTo(productPrice);
    }

    @Test
    @DisplayName("상품을 삭제한다.")
    void deleteProductTest() {
        // given : 상품 생성시 필요한 값 설정 및 상품 생성
        String productName = "고급 볼펜";
        Long productPrice = 1000L;

        Product product = Product.of(productName, productPrice);

        // when : 상품 삭제
        product.delete();

        // then : 삭제 여부 검증
        assertThat(product.getDeleted()).isTrue();
    }


}