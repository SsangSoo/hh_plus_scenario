package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RemoveProductServiceIntegrationTest extends SpringBootTestSupport {

    @AfterEach
    void tearDown() {
        productJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("상품을 삭제한 후엔 상품과 재고를 찾을 수 없다.")
    void deleteProductAfterProductNotFoundTest() {
        // given : 상품 생성 및 재고 생성
        String productName = "고급볼펜";
        Long productPrice = 1000L;

        // Product 생성
        ProductResponse productResponse = registerProductUseCase.register(new RegisterProductServiceRequest(productName, productPrice));

        // when
        removeProductUseCase.removeProduct(productResponse.getId());

        // then
        Assertions.assertThatThrownBy(() -> retrievePointUseCase.retrieve(productResponse.getId()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.NOT_FOUND_MEMBER.getMessage());
    }
}
