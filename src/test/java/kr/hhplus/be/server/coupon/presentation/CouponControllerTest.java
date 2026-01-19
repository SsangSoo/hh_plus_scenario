package kr.hhplus.be.server.coupon.presentation;

import kr.hhplus.be.server.config.RestDocsControllerSupport;
import kr.hhplus.be.server.coupon.application.usecase.IssueCouponUseCase;
import kr.hhplus.be.server.coupon.application.usecase.RegisterCouponUseCase;
import kr.hhplus.be.server.coupon.application.usecase.RetrieveCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.presentation.dto.request.IssueCouponRequest;
import kr.hhplus.be.server.coupon.presentation.dto.request.RegisterCouponRequest;
import kr.hhplus.be.server.coupon.presentation.dto.response.CouponResponse;
import kr.hhplus.be.server.coupon.presentation.dto.response.IssueCouponResponse;
import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CouponControllerTest extends RestDocsControllerSupport {

    private final RegisterCouponUseCase registerCouponUseCase = mock(RegisterCouponUseCase.class);
    private final RetrieveCouponUseCase retrieveCouponUseCase = mock(RetrieveCouponUseCase.class);
    private final IssueCouponUseCase issueCouponUseCase = mock(IssueCouponUseCase.class);


    @Override
    protected Object initContoller() {
        return new CouponController(registerCouponUseCase, retrieveCouponUseCase, issueCouponUseCase);
    }

    @Test
    void registerCoupon() throws Exception {
        // given
        String couponNumber = "abcde12345acv";
        LocalDate expiryDate = LocalDate.now().plusDays(10L);
        Integer amount = 10;
        Integer discountRate = 10;

        RegisterCouponRequest request = new RegisterCouponRequest(couponNumber, expiryDate, amount, discountRate);

        // 쿠폰 생성
        Coupon coupon = Coupon.create(couponNumber, expiryDate, amount, discountRate);
        coupon.assignId(1L);

        given(registerCouponUseCase.register(any()))
                .willReturn(CouponResponse.from(coupon));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/coupon")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.couponId").value(coupon.getId()))
                .andExpect(jsonPath("$.coupon").value(coupon.getCoupon()))
                .andExpect(jsonPath("$.expiryDate").value(coupon.getExpiryDate().toString()))
                .andExpect(jsonPath("$.amount").value(coupon.getAmount()))
                .andExpect(jsonPath("$.discountRate").value(coupon.getDiscountRate()))
                .andDo(document("쿠폰 생성",
                        requestFields(
                                fieldWithPath("coupon").description("쿠폰 번호"),
                                fieldWithPath("expiryDate").description("쿠폰 유효기간"),
                                fieldWithPath("amount").description("쿠폰 수량"),
                                fieldWithPath("discountRate").description("해당 쿠폰 할인율")
                        ),
                        responseFields(
                                fieldWithPath("couponId").description("쿠폰 Id"),
                                fieldWithPath("coupon").description("쿠폰 번호"),
                                fieldWithPath("expiryDate").description("쿠폰 유효기간"),
                                fieldWithPath("amount").description("쿠폰 수량"),
                                fieldWithPath("discountRate").description("해당 쿠폰 할인율")
                        )
                ));
    }

    @Test
    void retrieveCoupon() throws Exception {
        // given
        String couponNumber = "abcde12345acv";
        LocalDate expiryDate = LocalDate.now().plusDays(10L);
        Integer amount = 10;
        Integer discountRate = 10;

        Coupon coupon = Coupon.create(couponNumber, expiryDate, amount, discountRate);
        coupon.assignId(1L);

        given(retrieveCouponUseCase.retrieve(any()))
                .willReturn(CouponResponse.from(coupon));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/coupon/{couponId}", coupon.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponId").value(coupon.getId()))
                .andExpect(jsonPath("$.coupon").value(coupon.getCoupon()))
                .andExpect(jsonPath("$.expiryDate").value(coupon.getExpiryDate().toString()))
                .andExpect(jsonPath("$.amount").value(coupon.getAmount()))
                .andExpect(jsonPath("$.discountRate").value(coupon.getDiscountRate()))
                .andDo(document("쿠폰 조회",
                        pathParameters(
                                parameterWithName("couponId").description("조회할 쿠폰 Id")
                        ),
                        responseFields(
                                fieldWithPath("couponId").description("쿠폰 Id"),
                                fieldWithPath("coupon").description("쿠폰 번호"),
                                fieldWithPath("expiryDate").description("쿠폰 유효기간"),
                                fieldWithPath("amount").description("쿠폰 수량"),
                                fieldWithPath("discountRate").description("해당 쿠폰 할인율")
                        )
                ));
    }


    @Test
    void issueCoupon() throws Exception {
        // given
        Long couponId = 1L;
        Long memberId = 1L;
        Long couponHistoryId = 1L;

        CouponHistory couponHistory = CouponHistory.create(couponId, memberId);
        couponHistory.assignId(couponHistoryId);

        given(issueCouponUseCase.issue(any()))
                .willReturn(IssueCouponResponse.from(couponHistory));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/coupon/issue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(new IssueCouponRequest(couponId, memberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponHistoryId").value(couponHistory.getId()))
                .andExpect(jsonPath("$.couponId").value(couponHistory.getCouponId()))
                .andExpect(jsonPath("$.memberId").value(couponHistory.getMemberId()))
                .andExpect(jsonPath("$.couponIssuance").value(couponHistory.getCouponIssuance().withNano(0).toString()))
                .andExpect(jsonPath("$.couponUsed").value(couponHistory.isCouponUsed()))
                .andDo(document("쿠폰 발생",
                        requestFields(
                                fieldWithPath("couponId").description("쿠폰 Id"),
                                fieldWithPath("memberId").description("회원 Id")
                        ),
                        responseFields(
                                fieldWithPath("couponHistoryId").description("쿠폰 발행 내역 Id"),
                                fieldWithPath("couponId").description("쿠폰 Id"),
                                fieldWithPath("memberId").description("회원 Id"),
                                fieldWithPath("couponIssuance").description("쿠폰 발행 일자 및 시간"),
                                fieldWithPath("couponUsed").description("쿠폰 사용 여부")
                        )
                ));

    }


}