package kr.hhplus.be.server.domain.global.exception;

public class BusinessLogicRuntimeException extends RuntimeException {

    public BusinessLogicRuntimeException(String message) {
        super(message);
    }

    public BusinessLogicRuntimeException(BusinessLogicMessage businessLogicMessage) {
        super(businessLogicMessage.getMessage());
    }
}
