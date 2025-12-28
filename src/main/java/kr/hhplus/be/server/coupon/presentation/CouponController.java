package kr.hhplus.be.server.coupon.presentation;

import kr.hhplus.be.server.coupon.application.usecase.IssueCouponUseCase;
import kr.hhplus.be.server.coupon.application.usecase.RegisterCouponUseCase;
import kr.hhplus.be.server.coupon.application.usecase.RetrieveCouponUseCase;
import kr.hhplus.be.server.coupon.presentation.dto.request.IssueCouponRequest;
import kr.hhplus.be.server.coupon.presentation.dto.request.CouponIsuueRequest;
import kr.hhplus.be.server.coupon.presentation.dto.request.RegisterCouponRequest;
import kr.hhplus.be.server.coupon.presentation.dto.response.IssueCouponResponse;
import kr.hhplus.be.server.coupon.presentation.dto.response.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon")
public class CouponController {

    private final RegisterCouponUseCase registerCouponUseCase;
    private final RetrieveCouponUseCase retrieveCouponUseCase;
    private final IssueCouponUseCase issueCouponUseCase;

    @PostMapping
    public ResponseEntity<CouponResponse> register(@RequestBody RegisterCouponRequest request) {
        CouponResponse registered = registerCouponUseCase.register(request.toServiceRequest());

        return new ResponseEntity<>(registered, HttpStatus.CREATED);
    }

    @GetMapping("/{coupon-id}")
    public ResponseEntity<CouponResponse> retrieve(@PathVariable("coupon-id") Long couponId) {
        CouponResponse retrieved = retrieveCouponUseCase.retrieve(couponId);

        return ResponseEntity.ok(retrieved);
    }


    @PostMapping("/issue")
    public ResponseEntity<IssueCouponResponse> issue(@RequestBody IssueCouponRequest issueCouponRequest) {
        issueCouponUseCase.issue(issueCouponRequest.toServiceRequest());
    }


}
