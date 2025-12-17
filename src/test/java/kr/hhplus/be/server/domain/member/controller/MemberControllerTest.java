package kr.hhplus.be.server.domain.member.controller;

import kr.hhplus.be.server.config.RestDocsControllerSupport;
import kr.hhplus.be.server.member.presentation.MemberController;
import kr.hhplus.be.server.member.presentation.dto.request.RegisterMemberRequest;
import kr.hhplus.be.server.member.application.service.RegisterMemberService;
import kr.hhplus.be.server.member.application.dto.MemberResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MemberControllerTest extends RestDocsControllerSupport {

    private final RegisterMemberService memberService = mock(RegisterMemberService.class);

    @Override
    protected Object initContoller() {
        return new MemberController(memberService);
    }

    @Test
    @DisplayName("멤버 생성 API")
    void registerMamber() throws Exception {
        // given
        String name = "name";
        LocalDate birthDay = LocalDate.of(1998,1,1);
        String address = "address";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        RegisterMemberRequest request = new RegisterMemberRequest(name, birthDay, address);

        LocalDateTime registerTime = LocalDateTime.now().withNano(0);

        given(memberService.register(any()))
                .willReturn(MemberResult.of(1L, name, birthDay.format(formatter), address, registerTime, registerTime));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/member")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))

                )
                .andExpect(status().isCreated());



    }
}