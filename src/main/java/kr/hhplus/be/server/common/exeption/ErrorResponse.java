package kr.hhplus.be.server.common.exeption;


import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorResponse {

    private final String code;      // 예: "400"
    private final String message;   // 예: "입력값이 올바르지 않습니다."
    private final List<FieldError> errors;

    @Builder
    private ErrorResponse(String code, String message, List<FieldError> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .errors(new ArrayList<>())
                .build();
    }

    public static ErrorResponse of(String code, String message, List<FieldError> errors) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .errors(errors)
                .build();
    }

    @Getter
    @Builder
    public static class FieldError {
        private final String field; // 어느 필드에서 오류났는지
        private final String value; // 사용자가 전달한 값 (String으로 처리)
        private final String reason; // 왜 에러인지 (메시지)
    }
}
