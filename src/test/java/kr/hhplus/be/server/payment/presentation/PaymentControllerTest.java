package kr.hhplus.be.server.payment.presentation;

import kr.hhplus.be.server.config.RestDocsControllerSupport;
import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest  extends RestDocsControllerSupport {

    private final PaymentUseCase paymentUseCase = mock(PaymentUseCase.class);


    @Override
    protected Object initContoller() {
        return new PaymentController(paymentUseCase);
    }

    @Test
    void payment() throws Exception {
        // given
        Long orderId = 1L;
        Long memberId = 1L;
        Long paymentId = 1L;
        Long couponId = 1L;
        Long totalAmount = 10000L;

        // 결제 생성
        Payment payment = Payment.create(orderId, totalAmount, PaymentMethod.POINT);
        payment.assignId(paymentId);
        payment.changeState(PaymentState.PAYMENT_COMPLETE);

        PaymentServiceRequest paymentServiceRequest = new PaymentServiceRequest(orderId, memberId, paymentId, couponId);

        given(paymentUseCase.payment(paymentServiceRequest, UUID.randomUUID().toString()))
                .willReturn(PaymentResponse.from(payment));


        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/pay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(paymentServiceRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(payment.getId()))
                .andExpect(jsonPath("$.orderId").value(payment.getOrderId()))
                .andExpect(jsonPath("$.totalAmount").value(payment.getTotalAmount()))
                .andExpect(jsonPath("$.paymentState").value(payment.getPaymentState().name()))
                .andDo(document("결제",
                        requestFields(
                                fieldWithPath("orderId").description("주문 Id"),
                                fieldWithPath("memberId").description("회원 Id"),
                                fieldWithPath("paymentId").description("결제 Id"),
                                fieldWithPath("couponId").description("쿠폰 Id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("포인트 Id"),
                                fieldWithPath("orderId").description("회원 Id"),
                                fieldWithPath("totalAmount").description("결제 총 금액"),
                                fieldWithPath("paymentState").description("결제 상태")
                        )
                ));
    }

}