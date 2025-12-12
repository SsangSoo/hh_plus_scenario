package kr.hhplus.be.server.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
@Documented
public @interface ValidEnum {

    Class<? extends Enum<?>> enumClass();

    String message() default "올바르지 않은 값입니다";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
