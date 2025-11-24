package kr.hhplus.be.server.domain.member.controller.request;

import kr.hhplus.be.server.config.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class RegisterMemberRequestTest extends ControllerTestSupport {


    @Test
    @DisplayName("회원 생성시 이름은 필수다.")
    void registerMemberRequestValidNameTest() throws Exception {
        // given
        String name = "";

        RegisterMemberRequest request = new RegisterMemberRequest(name, LocalDate.now(), "주소");

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/member")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("요청 값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].value").value(""))
                .andExpect(jsonPath("$.errors[0].reason").value("이름은 필수입니다."));
    }


}