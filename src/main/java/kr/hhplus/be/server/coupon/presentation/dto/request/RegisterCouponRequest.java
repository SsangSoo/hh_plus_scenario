package kr.hhplus.be.server.coupon.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kr.hhplus.be.server.common.validation.ValidDate;
import kr.hhplus.be.server.coupon.application.dto.request.RegisterCouponServiceRequest;

import java.time.LocalDate;

public record RegisterCouponRequest(

        @NotBlank(message = "필수 값입니다.")
        @Size(min = 10, max = 30, message = "쿠폰 길이는 10~30 자여야 합니다.")
        String coupon,

        @ValidDate(message = "쿠폰의 유효기간은 오늘보다 하루 이상되어야 합니다.")
        LocalDate expiryDate,

        @Positive(message = "쿠폰의 생성 개수는 1개 이상이어야 한다.")
        Integer amount,

        @Positive(message = "할인율은 0보다 커야 합니다.")
        Integer discountRate
) {

    public RegisterCouponServiceRequest toServiceRequest() {
        return new RegisterCouponServiceRequest(
                this.coupon,
                this.expiryDate,
                this.amount,
                this.discountRate
        );
    }

}
