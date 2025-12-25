package kr.hhplus.be.server.order.presentation;

import kr.hhplus.be.server.config.RestDocsControllerSupport;
import kr.hhplus.be.server.order.application.service.PlaceOrderService;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.presentation.dto.request.OrderProductRequest;
import kr.hhplus.be.server.order.presentation.dto.request.OrderRequest;
import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.order.presentation.dto.response.OrderResponse;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
class OrderControllerTest extends RestDocsControllerSupport {

    private final PlaceOrderService orderService = mock(PlaceOrderService.class);

    @Override
    protected Object initContoller() {
        return new OrderController(orderService);
    }


    @Test
    void orderTest() throws Exception {
        // given : OrderResponse를 만들기위한 설정
        Long memberId = 1L;
        Long productId = 1L;
        Long quantity = 3L;
        String paymentMethod = "POINT";

        OrderRequest request = new OrderRequest(memberId, List.of(new OrderProductRequest(productId, quantity)), paymentMethod);

        Long orderId = 1L;
        Order order = Order.create(memberId);
        order.assignId(orderId);

        Long paymentId = 1L;
        Payment payment = Payment.create(orderId, 4000L * quantity, PaymentMethod.valueOf(paymentMethod));
        payment.assignId(paymentId);

        OrderResponse response = OrderResponse.from(order, PaymentResponse.from(payment));

        given(orderService.order(any()))
                .willReturn(response);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.orderDate").value(response.getOrderDate()))
                .andExpect(jsonPath("$.paymentId").value(payment.getId()))
                .andExpect(jsonPath("$.totalAmount").value(payment.getTotalAmount()))
                .andExpect(jsonPath("$.paymentState").value(payment.getPaymentState().toString()))
                .andDo(document("상품 주문 및 결제",
                        requestFields(
                                fieldWithPath("memberId").description("회원 Id"),
                                fieldWithPath("orderProductsRequest[].productId").description("회원 Id"),
                                fieldWithPath("orderProductsRequest[].quantity").description("주문 상품 수량"),
                                fieldWithPath("paymentMethod").description("결제 방식")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("주문 Id"),
                                fieldWithPath("memberId").description("회원 Id"),
                                fieldWithPath("orderDate").description("주문 일시"),
                                fieldWithPath("paymentId").description("결제 Id"),
                                fieldWithPath("totalAmount").description("총 주문 금액"),
                                fieldWithPath("paymentState").description("결제 상태")
                        )
                ));
    }
}