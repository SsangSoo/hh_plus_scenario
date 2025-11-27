package kr.hhplus.be.server.domain.point.controller.request;

import kr.hhplus.be.server.config.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChargePointRequestTest  extends ControllerTestSupport {


    @Test
    @DisplayName("포인트 충전시 0이하의 값은 올 수 없다.(음수)")
    void chargePointValidPointTest() throws Exception {
        // given
        Long memberId = 1L;
        Long negativeChargePoint = -1L;

        ChargePointRequest request = new ChargePointRequest(memberId, negativeChargePoint);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/point/charge")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("요청 값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors[0].field").value("chargePoint"))
                .andExpect(jsonPath("$.errors[0].value").value("-1"))
                .andExpect(jsonPath("$.errors[0].reason").value("충전 포인트 금액이 유효하지 않습니다. 다시 확인해주세요"));
    }

    @Test
    @DisplayName("포인트 충전시 0이하의 값은 올 수 없다.(0)")
    void chargePointValidPointTest2() throws Exception {
        // given
        Long memberId = 1L;
        Long zeroChargePoint = 0L;

        ChargePointRequest request = new ChargePointRequest(memberId, zeroChargePoint);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/point/charge")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("요청 값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors[0].field").value("chargePoint"))
                .andExpect(jsonPath("$.errors[0].value").value("0"))
                .andExpect(jsonPath("$.errors[0].reason").value("충전 포인트 금액이 유효하지 않습니다. 다시 확인해주세요"));
    }

    @Test
    @DisplayName("포인트 충전시 회원의 Id도 유효한 값이어야 한다.")
    void chargePointValidMemberIdTest() throws Exception {
        // given
        Long memberId = 0L;
        Long ChargePoint = 100L;

        ChargePointRequest request = new ChargePointRequest(memberId, ChargePoint);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/point/charge")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("요청 값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors[0].field").value("memberId"))
                .andExpect(jsonPath("$.errors[0].value").value("0"))
                .andExpect(jsonPath("$.errors[0].reason").value("회원 Id가 올바르지 않은 값입니다. 다시 확인해주세요"));
    }

    @Test
    @DisplayName("포인트 충전시 회원의 Id와 충전 포인트 모두 유효해야 한다.")
    void chargePointValidTest() throws Exception {
        // given
        Long memberId = 0L;
        Long zeroChargePoint = 0L;

        ChargePointRequest request = new ChargePointRequest(memberId, zeroChargePoint);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/point/charge")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("요청 값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors[?(@.field == 'memberId')].field").value("memberId"))
                .andExpect(jsonPath("$.errors[?(@.field == 'memberId')].value").value("0"))
                .andExpect(jsonPath("$.errors[?(@.field == 'memberId')].reason").value("회원 Id가 올바르지 않은 값입니다. 다시 확인해주세요"))
                .andExpect(jsonPath("$.errors[?(@.field == 'chargePoint')].field").value("chargePoint"))
                .andExpect(jsonPath("$.errors[?(@.field == 'chargePoint')].value").value("0"))
                .andExpect(jsonPath("$.errors[?(@.field == 'chargePoint')].reason").value("충전 포인트 금액이 유효하지 않습니다. 다시 확인해주세요"));
    }
}