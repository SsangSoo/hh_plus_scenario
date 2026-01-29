package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductQueryRepository;
import kr.hhplus.be.server.product.infrastructure.persistence.query.ProductProjection;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RetrieveProductServiceTest {

    @Mock
    ProductQueryRepository productQueryRepository;

    RetrieveProductService retrieveProductService;

    @BeforeEach
    void setUp() {
        retrieveProductService = new RetrieveProductService(productQueryRepository);
    }


    @Test
    @DisplayName("상품을 조회할 수 있다.")
    void retrieveProductTest() {
        // given : 상품 조회 검증시 확인할 값들을 설정(id, 상품 이름, 상품 가격, 재고)
        Long productId = 1L;
        String productName = "고급볼펜";
        Long productPrice = 1000L;
        Long productQuantity = 23L;

        Product product = Product.create(new RegisterProductServiceRequest(productName, productPrice));
        product.assignId(productId);

        ProductProjection productProjection = new ProductProjection(productId, productName, productPrice, productQuantity);

        given(productQueryRepository.retrieveProductJoinStock(anyLong()))
                .willReturn(Optional.ofNullable(productProjection));

        // when : service 에서 상품 조회 호출
        ProductResponse response =  retrieveProductService.retrieveProduct(1L);

        // then : given에서 검증하고자 했던 값들 확인
        assertThat(response.getId()).isEqualTo(productId);
        assertThat(response.getProductName()).isEqualTo(productName);
        assertThat(response.getPrice()).isEqualTo(productPrice);
        assertThat(response.getQuantity()).isEqualTo(productQuantity);

        // 메서드 호출 횟수 검증
        then(productQueryRepository).should(times(1)).retrieveProductJoinStock(productId);
    }


    @Test
    @DisplayName("요청받은 상품Id로 상품을 찾을 수 없는 경우 예외가 발생한다.")
    void retrieveProductShouldThrowExceptionWhenProductDoesNotExistTest() {
        // given : 존재하지 않는 상품 Id값
        Long notFoundProductId = 3L;

        // 상품 id로 재고 조회
        given(productQueryRepository.retrieveProductJoinStock(notFoundProductId))
                .willThrow(new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_PRODUCT));

        // when // then
        Assertions.assertThatThrownBy(() -> retrieveProductService.retrieveProduct(notFoundProductId))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage("상품을 찾을 수 없습니다.");

        // 메서드 호출 횟수 검증
        then(productQueryRepository).should(times(1)).retrieveProductJoinStock(notFoundProductId);
    }
}