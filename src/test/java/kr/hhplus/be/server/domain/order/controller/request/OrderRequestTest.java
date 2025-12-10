package kr.hhplus.be.server.domain.order.controller.request;

import kr.hhplus.be.server.config.ControllerTestSupport;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OrderRequestTest extends ControllerTestSupport {

    @Test
    @DisplayName("결제 방식은 정해진 방식을 따라야 한다.")
    void orderRequestPaymentMethodTest() {
        // given
        Long memberId = 1L;
        Long productId = 1L;
        Long quantity = 1L;

        String paymentMethod = "POINTA";

        OrderProductRequest orderProductRequest = new OrderProductRequest(productId, quantity);
        OrderRequest request = new OrderRequest(memberId, orderProductRequest, paymentMethod);

        // when // then
        assertThatThrownBy(() -> request.toServiceRequest())
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage("지원하지 않는 결제 방식입니다.");
    }

}