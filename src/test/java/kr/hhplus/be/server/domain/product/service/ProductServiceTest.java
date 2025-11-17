package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.global.exception.BusinessLogicRuntimeException;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.service.response.ProductResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    ProductService productService;


    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }


    @Test
    @DisplayName("상품을 조회할 수 있다.")
    void retrieveProductTest() {
        // given : 상품 조회 검증시 확인할 값들을 설정(id, 상품 이름, 상품 가격, 재고)
        Long productId = 1L;
        String productName = "고급볼펜";
        Long productPrice = 1000L;
        Long productQuantity = 23L;

        Product product = Product.of(productName, productPrice);
        setId(product, productId);

        // id로 상품 조회
        given(productRepository.findById(anyLong()))
                .willReturn(Optional.of(product));

        // 상품 id로 재고 조회
        given(productRepository.retrieveStockByProductId(anyLong()))
                .willReturn(productQuantity);

        // when : service 에서 상품 조회 호출
        ProductResponse response =  productService.retrieve(1L);

        // then : given에서 검증하고자 했던 값들 확인
        assertThat(response.getId()).isEqualTo(productId);
        assertThat(response.getName()).isEqualTo(productName);
        assertThat(response.getPrice()).isEqualTo(productPrice);
        assertThat(response.getQuantity()).isEqualTo(productQuantity);

        // 메서드 호출 횟수 검증
        then(productRepository).should(times(1)).findById(productId);
        then(productRepository).should(times(1)).retrieveStockByProductId(productId);

    }

    @Test
    @DisplayName("요청받은 상품Id로 상품을 찾을 수 없는 경우 예외가 발생한다.")
    void retrieveProductShouldThrowExceptionWhenProductDoesNotExistTest() {
        // given : 존재하지 않는 상품 Id값
        Long notFoundProductId = 3L;

        // 상품 id로 재고 조회
        given(productRepository.findById(notFoundProductId))
                .willReturn(Optional.empty());

        // when // then : service 에서 존재하지 않는 상품 id로 조회시 예외가 발생한다.
        Assertions.assertThatThrownBy(() -> productService.retrieve(notFoundProductId))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage("상품을 찾을 수 없습니다.");

        // 메서드 호출 횟수 검증
        then(productRepository).should(times(1)).findById(notFoundProductId);
        then(productRepository).should(times(0)).retrieveStockByProductId(notFoundProductId);
    }

    @Test
    @DisplayName("재고가 없을 경우, 재고 Entity를 생성하고, 0으로 반환한다.")
    void ifOutOfStockCreateStockAndReturnZeroTest () {
        // given : 상품 조회 검증시 확인할 값들을 설정(id, 상품 이름, 상품 가격, 재고)
        Long productId = 1L;
        String productName = "고급볼펜";
        Long productPrice = 1000L;
        Long productQuantity = 0L;

        Product product = Product.of(productName, productPrice);
        setId(product, productId);

        // id로 상품 조회
        given(productRepository.findById(anyLong()))
                .willReturn(Optional.of(product));

        // 상품 id로 재고 조회
        given(productRepository.retrieveStockByProductId(anyLong()))
                .willReturn(null);

        // when : service 에서 상품 조회 호출
        ProductResponse response =  productService.retrieve(1L);

        // then : given에서 검증하고자 했던 값들 확인
        assertThat(response.getId()).isEqualTo(productId);
        assertThat(response.getName()).isEqualTo(productName);
        assertThat(response.getPrice()).isEqualTo(productPrice);
        assertThat(response.getQuantity()).isEqualTo(productQuantity);

        // 메서드 호출 횟수 검증
        then(productRepository).should(times(1)).findById(productId);
        then(productRepository).should(times(1)).retrieveStockByProductId(productId);

    }

    /**
     * Test에서만 사용할 id 주입을 위한 메서드
     * @param entity
     * @param id
     * @return
     * @param <T>
     */
    public static <T> T setId(T entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

}