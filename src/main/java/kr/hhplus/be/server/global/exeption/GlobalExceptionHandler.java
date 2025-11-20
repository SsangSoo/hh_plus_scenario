package kr.hhplus.be.server.global.exeption;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @RequestBody DTO 검증 실패
     * ( @Valid / @Validated + @RequestBody )
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        ErrorResponse errorResponse = ErrorResponse.of(
                "400",
                "요청 값이 유효하지 않습니다.",
                toFieldErrors(ex.getBindingResult())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {

        ErrorResponse errorResponse = ErrorResponse.of(
                "400",
                "요청 값이 유효하지 않습니다.",
                toFieldErrors(ex.getBindingResult())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {

        List<ErrorResponse.FieldError> errors = ex.getConstraintViolations().stream()
                .map(this::toFieldError)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.of(
                "400",
                "요청 값이 유효하지 않습니다.",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 기타 예상 못한 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {

        ErrorResponse errorResponse = ErrorResponse.of(
                "500",
                "서버 내부 오류가 발생했습니다."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }


    private List<ErrorResponse.FieldError> toFieldErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(this::toFieldError)
                .collect(Collectors.toList());
    }

    private ErrorResponse.FieldError toFieldError(FieldError fieldError) {
        String value = fieldError.getRejectedValue() == null ? "" : fieldError.getRejectedValue().toString();

        return ErrorResponse.FieldError.builder()
                .field(fieldError.getField())
                .value(value)
                .reason(fieldError.getDefaultMessage())
                .build();
    }

    private ErrorResponse.FieldError toFieldError(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        String fieldName = extractFieldName(path);

        return ErrorResponse.FieldError.builder()
                .field(fieldName)
                .value(violation.getInvalidValue() == null ? "" : violation.getInvalidValue().toString())
                .reason(violation.getMessage())
                .build();
    }

    private String extractFieldName(String propertyPath) {
        int idx = propertyPath.lastIndexOf('.');
        if (idx == -1) {
            return propertyPath;
        }
        return propertyPath.substring(idx + 1);
    }
}