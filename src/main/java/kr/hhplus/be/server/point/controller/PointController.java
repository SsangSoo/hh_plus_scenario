package kr.hhplus.be.server.point.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.point.controller.request.ChargePointRequest;
import kr.hhplus.be.server.point.service.PointService;
import kr.hhplus.be.server.point.service.response.PointResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point")
public class PointController {

    private final PointService pointService;

    @PostMapping("/charge")
    public ResponseEntity<PointResponse> charge(@RequestBody @Valid ChargePointRequest request) {
        PointResponse response = pointService.charge(request.toChargePoint());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<PointResponse> retrieve(@PathVariable("memberId") Long memberId) {
        PointResponse response = pointService.retrieve(memberId);
        return  ResponseEntity.ok(response);
    }
}
