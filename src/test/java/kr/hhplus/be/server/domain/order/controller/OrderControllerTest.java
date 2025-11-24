package kr.hhplus.be.server.domain.order.controller;

import kr.hhplus.be.server.config.RestDocsControllerSupport;
import kr.hhplus.be.server.config.Util;
import kr.hhplus.be.server.domain.order.controller.request.OrderProductRequest;
import kr.hhplus.be.server.domain.order.controller.request.OrderRequest;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.order.service.response.OrderResponse;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.service.request.PaymentServiceRequest;
import kr.hhplus.be.server.domain.payment.service.response.PaymentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest extends RestDocsControllerSupport {

    private final OrderService orderService = mock(OrderService.class);

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

        OrderRequest request = new OrderRequest(memberId, new OrderProductRequest(productId, quantity));

        Long orderId = 1L;
        Order order = Order.rigester(memberId);
        Util.setId(order, orderId);

        Long paymentId = 1L;
        Payment payment = Payment.register(new PaymentServiceRequest(orderId, 4000 * quantity));
        Util.setId(payment, paymentId);


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
                .andExpect(jsonPath("$.orderDate").value(response.getOrderDate().toString()))
                .andExpect(jsonPath("$.paymentId").value(payment.getId()))
                .andExpect(jsonPath("$.totalAmount").value(payment.getTotalAmount()))
                .andExpect(jsonPath("$.paymentState").value(payment.getPaymentState().toString()))
                .andDo(document("상품 주문 및 결제",
                        requestFields(
                                fieldWithPath("memberId").description("회원 Id"),
                                fieldWithPath("orderProductRequest.productId").description("상품 Id"),
                                fieldWithPath("orderProductRequest.quantity").description("주문 상품 수량")
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