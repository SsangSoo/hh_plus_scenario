package kr.hhplus.be.server.domain.member.controller.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class RegisterMemberRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(InvalidRegisterMemberRequestProvider.class)
    @DisplayName("RegisterMemberRequest 검증 실패 케이스")
    void RegisterMemberValidationFailure(
            String testCase,
            RegisterMemberRequest request,
            String expectedField,
            String expectedMessage
    ) {
        // when
        Set<ConstraintViolation<RegisterMemberRequest>> violations =
                validator.validate(request);

        // then
        assertThat(violations).hasSize(1);

        ConstraintViolation<RegisterMemberRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo(expectedField);
        assertThat(violation.getMessage()).isEqualTo(expectedMessage);
    }



}