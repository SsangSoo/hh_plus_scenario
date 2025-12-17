package kr.hhplus.be.server.domain.point.controller;

import kr.hhplus.be.server.config.RestDocsControllerSupport;
import kr.hhplus.be.server.config.Util;
import kr.hhplus.be.server.point.controller.PointController;
import kr.hhplus.be.server.point.controller.request.ChargePointRequest;
import kr.hhplus.be.server.point.entity.Point;
import kr.hhplus.be.server.point.service.PointService;
import kr.hhplus.be.server.point.service.request.ChargePoint;
import kr.hhplus.be.server.point.service.response.PointResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PointControllerTest extends RestDocsControllerSupport {

    private final PointService pointService = mock(PointService.class);

    @Override
    protected Object initContoller() {
        return new PointController(pointService);
    }

    @Test
    void chargePointTest() throws Exception {
        // given
        Long pointId = 1L;
        Long memberId = 1L;
        Long chargePoint = 3000L;

        // 포인트 생성
        Point point = Point.register(memberId);
        Util.setId(point, pointId);

        // 포인트 충전
        ChargePointRequest request = new ChargePointRequest(memberId, chargePoint);
        point.charge(request.toChargePoint());

        given(pointService.charge(any()))
                .willReturn(PointResponse.from(point));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/point/charge")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pointId))
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.point").value(point.getPoint()))
                .andDo(document("포인트 충전",
                        requestFields(
                                fieldWithPath("memberId").description("회원 Id"),
                                fieldWithPath("chargePoint").description("충전 포인트")
                        ),
                        responseFields(
                                fieldWithPath("id").description("포인트 Id"),
                                fieldWithPath("memberId").description("회원 Id"),
                                fieldWithPath("point").description("충전 후 point")
                        )
                ));
    }



    @Test
    void retrievePointTest() throws Exception {
        // given
        Long pointId = 1L;
        Long memberId = 1L;
        Long chargePoint = 3000L;

        // 포인트 생성
        Point point = Point.register(memberId);
        Util.setId(point, pointId);

        // 포인트 조회
        point.charge(new ChargePoint(memberId, chargePoint));

        // 포인트 조회
        given(pointService.retrieve(any()))
                .willReturn(PointResponse.from(point));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/point/{memberId}", memberId)                                .accept(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pointId))
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.point").value(point.getPoint()))
                .andDo(document("포인트 조회",
                        pathParameters(
                                parameterWithName("memberId").description("조회할 회원 Id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("포인트 Id"),
                                fieldWithPath("memberId").description("회원 Id"),
                                fieldWithPath("point").description("충전 후 point")
                        )
                ));
    }



}