package kr.hhplus.be.server.common.exeption;

import kr.hhplus.be.server.config.ControllerTestSupport;
import kr.hhplus.be.server.point.presentation.dto.request.ChargePointRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ErrorResponseTest extends ControllerTestSupport {

    @Test
    @DisplayName("ErrorResponse는 요청에 대한 여러 개의 필드를 검증할 수 있다.")
    void ErrorResponseTest() throws Exception {
        // given
        ChargePointRequest request = new ChargePointRequest(null, null);

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
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors[*].field").value(containsInAnyOrder("memberId", "chargePoint")))
                .andExpect(jsonPath("$.errors[?(@.field=='memberId')].reason").value("사용자 Id는 필수입니다."))
                .andExpect(jsonPath("$.errors[?(@.field=='chargePoint')].reason").value("포인트 충전시 충전 금액은 필수입니다."));
    }


}