package kr.hhplus.be.server.global.exeption.business;

import lombok.Getter;

public enum BusinessLogicMessage {

    NOT_FOUND_PRODUCT("상품을 찾을 수 없습니다."),
    NOT_FOUND_STOCK("재고를 찾을 수 없습니다."),

    NOT_FOUND_MEMBER("회원을 찾을 수 없습니다."),
    NOT_FOUND_MEMBER_POINT("회원의 포인트를 찾을 수 없습니다."),

    CHARGE_POINT_NOT_POSITIVE("충전하려는 포인트는 0 이상이어야 합니다.");



    @Getter
    final String message;

    BusinessLogicMessage(String message) {
        this.message = message;
    }
}
