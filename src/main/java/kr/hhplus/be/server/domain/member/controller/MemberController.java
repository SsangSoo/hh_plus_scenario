package kr.hhplus.be.server.domain.member.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.member.controller.request.RegisterMemberRequest;
import kr.hhplus.be.server.domain.member.service.MemberService;
import kr.hhplus.be.server.domain.member.service.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> register(@RequestBody @Valid RegisterMemberRequest request) {
        MemberResponse response = memberService.register(request.toServiceRequest());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
