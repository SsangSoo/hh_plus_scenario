package kr.hhplus.be.server.point.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.point.application.usecase.ChargePointUseCase;
import kr.hhplus.be.server.point.application.usecase.RetrievePointUseCase;
import kr.hhplus.be.server.point.presentation.dto.request.ChargePointRequest;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point")
public class PointController {

    private final ChargePointUseCase chargePointUseCase;
    private final RetrievePointUseCase retrievePointUseCase;

    @PostMapping("/charge")
    public ResponseEntity<PointResponse> charge(@RequestBody @Valid ChargePointRequest request) {
        PointResponse response = chargePointUseCase.charge(request.toChargePoint());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<PointResponse> retrieve(@PathVariable("memberId") Long memberId) {
        PointResponse response = retrievePointUseCase.retrieve(memberId);
        return  ResponseEntity.ok(response);
    }
}
