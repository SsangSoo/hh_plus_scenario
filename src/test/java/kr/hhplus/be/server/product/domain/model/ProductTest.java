package kr.hhplus.be.server.product.domain.model;

import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


class ProductTest {

    @Test
    @DisplayName("상품을 요청으로부터 생성을 할 수 있다(상품 생성 테스트)")
    void productCreateTest() {
        // given : 상품 생성시 필요한 값 설정
        String productName = "고급 볼펜";
        Long productPrice = 1000L;

        // when : 상품 생성
        Product product = Product.create(new RegisterProductServiceRequest(productName, productPrice));

        // then : 상품의 생성 여부 / 생성 시 주입된 값들 검증
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo(productName);
        assertThat(product.getPrice()).isEqualTo(productPrice);
        assertThat(product.getId()).isNull();
    }


    @Test
    @DisplayName("of 으로 테스트")
    void ofTest() {
        Product product = Product.of(1L, "상품", 3000L );

        assertThat(product.getId()).isNotNull();
        assertThat(product.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("id를 메서드로 추가할 수 있다.")
    void assignIdTest() {
        Product product = Product.create(new RegisterProductServiceRequest("상품", 3000L));

        assertThat(product.getId()).isNull();

        product.assignId(1L);

        assertThat(product.getId()).isNotNull();
        assertThat(product.getId()).isEqualTo(1L);
    }

}