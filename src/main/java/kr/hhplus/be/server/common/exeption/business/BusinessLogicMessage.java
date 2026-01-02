package kr.hhplus.be.server.common.exeption.business;

import lombok.Getter;

public enum BusinessLogicMessage {

    NOT_FOUND_PRODUCT("상품을 찾을 수 없습니다."),
    NOT_FOUND_SOME_PRODUCT("찾을 수 없는 상품이 있습니다."),
    NOT_FOUND_STOCK("재고를 찾을 수 없습니다."),
    NOT_FOUND_COUPON("쿠폰을 찾을 수 없습니다."),

    NOT_FOUND_MEMBER("회원을 찾을 수 없습니다."),
    NOT_FOUND_MEMBER_POINT("회원의 포인트를 찾을 수 없습니다."),

    NOT_POSSIBLE_ISSUE_COUPON_BY_INSUFFICIENT_NUMBER("발행 가능한 쿠폰 개수가 부족하여, 발행할 수 없습니다."),

    ALREADY_HAVE_THIS_COUPON("현재 가지고 있는 쿠폰입니다."),

    CHARGE_POINT_NOT_POSITIVE("충전하려는 포인트는 0 이상이어야 합니다."),
    POINT_IS_NOT_ENOUGH("현재 가지고 있는 포인트가 부족합니다."),

    STOCK_IS_NOT_ENOUGH("현재 재고보다 차감하려는 재고가 많습니다."),
    UNSUPPORTED_PAYMENT_METHOD("지원하지 않는 결제 방식입니다."),
    ;



    @Getter
    final String message;

    BusinessLogicMessage(String message) {
        this.message = message;
    }
}
