package kr.hhplus.be.server.member.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.member.application.usecase.RegisterMemberUseCase;
import kr.hhplus.be.server.member.presentation.dto.request.RegisterMemberRequest;
import kr.hhplus.be.server.member.application.dto.MemberResult;
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

    private final RegisterMemberUseCase registerMemberUseCase;

    @PostMapping
    public ResponseEntity<MemberResult> register(@RequestBody @Valid RegisterMemberRequest request) {
        MemberResult response = registerMemberUseCase.register(request.toServiceRequest());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}