package kr.hhplus.be.server.domain.order.controller.request;

import kr.hhplus.be.server.config.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderProductRequestTest extends ControllerTestSupport {

    @Test
    @DisplayName("상품 Id는 0보다 커야 한다.")
    void productIdIsZeroVerifyTest() throws Exception {
        // given
        Long memberId = 1L;
        String paymentMethod = "POINT";
        Long productId = 0L;
        Long quantity = 1L;

        OrderProductRequest orderProductRequest = new OrderProductRequest(productId, quantity);
        OrderRequest request = new OrderRequest(memberId, orderProductRequest, paymentMethod);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("요청 값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors[0].field").value("orderProductRequest.productId"))
                .andExpect(jsonPath("$.errors[0].value").value("0"))
                .andExpect(jsonPath("$.errors[0].reason").value("유효하지 않은 값입니다. 상품 Id를 확인해주세요."));
    }

    @Test
    @DisplayName("상품 Id는 0보다 커야 한다.")
    void productIdIsMinusOneVerifyTest() throws Exception {
        // given
        Long memberId = 1L;
        String paymentMethod = "POINT";

        Long productId = -1L;
        Long quantity = 1L;

        OrderProductRequest orderProductRequest = new OrderProductRequest(productId, quantity);
        OrderRequest request = new OrderRequest(memberId, orderProductRequest, paymentMethod);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("요청 값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors[0].field").value("orderProductRequest.productId"))
                .andExpect(jsonPath("$.errors[0].value").value("-1"))
                .andExpect(jsonPath("$.errors[0].reason").value("유효하지 않은 값입니다. 상품 Id를 확인해주세요."));
    }

}