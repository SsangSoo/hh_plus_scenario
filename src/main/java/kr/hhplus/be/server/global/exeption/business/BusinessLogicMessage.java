package kr.hhplus.be.server.global.exeption.business;

import lombok.Getter;

public enum BusinessLogicMessage {

    NOT_FOUND_PRODUCT("상품을 찾을 수 없습니다."),
    NOT_FOUND_Stock("재고를 찾을 수 없습니다.");

    @Getter
    final String message;

    BusinessLogicMessage(String message) {
        this.message = message;
    }
}
