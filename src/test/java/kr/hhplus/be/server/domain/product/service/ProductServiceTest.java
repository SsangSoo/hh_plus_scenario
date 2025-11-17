package kr.hhplus.be.server.domain.product.service;

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
        // given
        Long productId = 1L;
        String productName = "고급볼펜";
        Long productPrice = 1000L;

        Product product = Product.of(productName, productPrice);
        setId(product, productId);

        given(productRepository.findById(anyLong()))
                .willReturn(Optional.of(product));

        // when
        ProductResponse response =  productService.retrieve(1L);

        // then
        assertThat(response.getId()).isEqualTo(productId);
        assertThat(response.getName()).isEqualTo(productName);
        assertThat(response.getPrice()).isEqualTo(productPrice);
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