package kr.hhplus.be.server.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
@Documented
public @interface ValidDate {

    String message() default "올바르지 않은 값입니다";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
