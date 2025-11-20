package kr.hhplus.be.server.global.exeption.business;

public class BusinessLogicRuntimeException extends RuntimeException {

    public BusinessLogicRuntimeException(String message) {
        super(message);
    }

    public BusinessLogicRuntimeException(BusinessLogicMessage businessLogicMessage) {
        super(businessLogicMessage.getMessage());
    }
}
