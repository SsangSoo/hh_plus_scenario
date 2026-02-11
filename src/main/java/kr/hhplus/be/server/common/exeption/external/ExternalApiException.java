package kr.hhplus.be.server.common.exeption.external;

public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String message) {
        super("외부 API 전송 실패");
    }
}
