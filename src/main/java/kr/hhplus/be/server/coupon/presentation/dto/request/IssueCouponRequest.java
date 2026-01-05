package kr.hhplus.be.server.coupon.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.coupon.application.dto.request.IssueCouponServiceRequest;

public record IssueCouponRequest(

        @NotBlank(message = "쿠폰 Id는 필수 값입니다.")
        @Positive(message = "쿠폰 Id는 0보다 커야합니다.")
        Long couponId,

        @NotBlank(message = "회원 Id는 필수 값입니다.")
        @Positive(message = "회원 Id는 0보다 커야합니다.")
        Long memberId
) {

    public IssueCouponServiceRequest toServiceRequest() {
        return new IssueCouponServiceRequest(this.couponId, this.memberId);
    }
}
