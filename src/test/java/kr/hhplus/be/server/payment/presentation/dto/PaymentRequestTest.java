package kr.hhplus.be.server.payment.presentation.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import kr.hhplus.be.server.point.presentation.dto.request.ChargePointRequest;
import kr.hhplus.be.server.point.presentation.request.InvalidChargePointRequestProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PaymentRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(InvalidPaymentRequestProvider.class)
    @DisplayName("PaymentRequest 검증 실패 케이스")
    void PaymentRequestValidationFailure(
            String testCase,
            PaymentRequest request,
            String expectedField,
            String expectedMessage
    ) {
        // when
        Set<ConstraintViolation<PaymentRequest>> violations =
                validator.validate(request);

        // then
        assertThat(violations).hasSize(1);

        ConstraintViolation<PaymentRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo(expectedField);
        assertThat(violation.getMessage()).isEqualTo(expectedMessage);
    }

}